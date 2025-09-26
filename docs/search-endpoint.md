# Recherche top bar – Backend Overview

Ce document décrit en détail la mise en place de l'endpoint `GET /search` ajouté pour alimenter la barre de recherche globale du front.

## 1. Objectifs fonctionnels

- **Supporter les carnets (`TravelDiary`) et étapes (`Step`) dans un même payload.**
- **Filtrer les résultats selon la visibilité :**
  - Toujours renvoyer les carnets publiés (`isPublished = true`) et non privés (`isPrivate = false` ou `NULL`).
  - Inclure les éléments privés ou non publiés seulement pour leur propriétaire authentifié.
- **Fournir les métadonnées nécessaires au front :**
  - Pour les carnets : `id`, `title`, `description`, `coverUrl` (cover visible uniquement).
  - Pour les étapes : `id`, `title`, `diaryId`, `diaryTitle`, `excerpt` (160 caractères max).

## 2. Architecture backend

```
+----------------+      GET /search?query=...      +------------------+
|  Front client  |  -----------------------------> |  SearchController |
+----------------+                                  +---------+--------+
                                                            |
                                                            v
                                                +-----------------------+
                                                |     SearchService     |
                                                +-----------+-----------+
                                                            |
                            +-------------------------------+-------------------------------+
                            v                                                               v
          +--------------------------------+                         +--------------------------------+
          | TravelDiaryRepository.search… |                         |    StepRepository.search…      |
          +--------------------------------+                         +--------------------------------+
                            |                                                               |
                            v                                                               v
                   +-----------------+                                            +-----------------+
                   |  travel_diary   |                                            |      step       |
                   +-----------------+                                            +-----------------+
```

- `SearchController` valide le paramètre `query` et délègue à `SearchService`.
- `SearchService` centralise la résolution de l'utilisateur courant, lance les deux requêtes et assemble la réponse (`SearchResponseDTO`).
- Les repositories SQL filtrent directement selon la visibilité.

## 3. Détails des composants

### 3.1 DTOs (`src/main/java/com/wcs/travel_blog/search/dto`)
- `SearchResponseDTO` : objet racine contenant deux listes (`diaries`, `steps`).
- `SearchDiaryDTO` : données essentielles d'un carnet et URL de couverture si visible.
- `SearchStepDTO` : métadonnées de l'étape + extrait tronqué.

### 3.2 Service (`SearchService`)
- Normalise la recherche (`trim`) et résout l'ID du visiteur courant via `SecurityContext`.
- Convertit les entités JPA en DTOs en contrôlant la visibilité de l'image de couverture et en construisant l'extrait utilisateur.
- Gestion des principaux cas d'authentification : entité `User`, `UserDetails` ou simple nom d'utilisateur.

### 3.3 Repositories
- `TravelDiaryRepository.searchVisibleDiaries(query, userId)`
  - Règle owner : `td.user.id = :userId`.
  - Règle public : `td.isPublished = true` et `(td.isPrivate IS NULL OR td.isPrivate = false)`.
  - Joint `media` pour exposer la cover sans requête paresseuse supplémentaire.
- `StepRepository.searchVisibleSteps(query, userId)`
  - Même logique de visibilité, appliquée via le carnet parent.

### 3.4 Contrôleur (`SearchController`)
- Rejette les requêtes sans `query` ou contenant uniquement des espaces (`400 BAD_REQUEST`).
- Retourne un `200 OK` avec le DTO agrégé.

## 4. Jeu d'essai et scénarios de test

### 4.1 Données de référence (`import.sql`)
Les entrées suivantes démontrent la logique :

| Type   | Titre                    | Propriétaire | isPublished | isPrivate | Status       |
|--------|-------------------------|--------------|-------------|-----------|--------------|
| Diary  | Tour du monde en solo   | Alice        | true        | false     | IN_PROGRESS  |
| Diary  | Escapade en Asie        | Bob          | true        | false     | COMPLETED    |
| Diary  | Road trip en Europe     | Charlie      | true        | false     | IN_PROGRESS  |

Même si « Tour du monde en solo » est `IN_PROGRESS`, il reste visible, car publié et public.

### 4.2 Scénarios manuels

1. **Visiteur anonyme** – `curl 'http://localhost:8080/search?query=tour'`
   - Renvoie « Tour du monde en solo » et ses étapes publiques.
2. **Utilisateur authentifié** – `curl -H "Authorization: Bearer <token>" 'http://localhost:8080/search?query=perso'`
   - Renvoie le carnet privé du propriétaire si le token correspond.
3. **Champ vide** – `curl 'http://localhost:8080/search'`
   - Échec contrôlé avec `400` (paramètre obligatoire).

### 4.3 Tests automatisés
- `SearchControllerIT` (`src/test/java/.../SearchControllerIT.java`)
  - **shouldReturnPublicResultsForAnonymousUser** : vérifie qu'un visiteur anonyme obtient les carnets publiés (y compris ceux `IN_PROGRESS`) et les étapes publiques.
  - **shouldReturnOwnerSpecificResultsWhenAuthenticated** : vérifie que le propriétaire authentifié voit ses contenus privés.
  - **shouldReturn400WhenQueryMissing** : valide la vérification d'entrée.

Commandes :
```bash
./mvnw test -Dspring.profiles.active=test
# en cas d'environnement sans accès au dépôt CodeArtifact, télécharger le parent Spring Boot en local au préalable.
```

## 5. Format de réponse

```json
{
  "diaries": [
    {
      "id": 1,
      "title": "Tour du monde en solo",
      "description": "Mon aventure autour du monde",
      "coverUrl": "https://.../cover.jpg"
    }
  ],
  "steps": [
    {
      "id": 3,
      "title": "Bangkok",
      "diaryId": 1,
      "diaryTitle": "Tour du monde en solo",
      "excerpt": "Street food et temples"
    }
  ]
}
```

### Notes supplémentaires
- Les URLs de cover ne sont renvoyées que si le média est marqué `isVisible = true`.
- Les champs peuvent être `null` si l'information n'existe pas (ex. étapes sans carnet chargé).
- Aucun score de pertinence : l'ordre est `updatedAt DESC` côté base.

## 6. Points d'extension
- Ajouter des filtres supplémentaires (thèmes, géolocalisation) nécessitera de nouvelles jointures dans les requêtes.
- Pour un vrai full-text ou un tri plus avancé, envisager une couche `@Query` native ou un moteur dédié (Elasticsearch).

