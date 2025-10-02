# Requêtes HQL liées aux carnets de voyage

Ce document décrit chaque requête personnalisée déclarée dans `TravelDiaryRepository` ainsi que la logique métier associée.

## `searchVisibleDiaries`
```jpql
SELECT DISTINCT td FROM TravelDiary td
LEFT JOIN FETCH td.media media
LEFT JOIN td.steps step
WHERE (
    LOWER(td.title) LIKE LOWER(CONCAT('%', :query, '%'))
    OR LOWER(td.description) LIKE LOWER(CONCAT('%', :query, '%'))
    OR LOWER(step.city) LIKE LOWER(CONCAT('%', :query, '%'))
    OR LOWER(step.country) LIKE LOWER(CONCAT('%', :query, '%'))
    OR LOWER(step.continent) LIKE LOWER(CONCAT('%', :query, '%'))
)
AND (
    (:userId IS NOT NULL AND td.user.id = :userId)
    OR (
        td.isPrivate = FALSE
        AND td.isPublished = TRUE
        AND SIZE(td.steps) > 0
    )
)
ORDER BY td.updatedAt DESC
```

1. **Sélection** : on récupère les carnets (`td`). `DISTINCT` évite les doublons en cas de médias multiples.
2. **Jointures** : `LEFT JOIN FETCH td.media` charge la couverture éventuelle, `LEFT JOIN td.steps` permet de filtrer sur les attributs de géolocalisation.
3. **Filtre texte** : la clause `WHERE` teste le titre, la description et les champs `city/country/continent` des étapes en insensible à la casse.
4. **Bloc propriétaire** : si `:userId` est fourni, les carnets de l'utilisateur connecté remontent, même privés ou non publiés.
5. **Bloc public** : sinon, on impose `isPrivate = FALSE`, `isPublished = TRUE` et au moins une étape via `SIZE(td.steps) > 0` (équivalent à « il existe une étape liée »).
6. **Tri** : retour du plus récent au plus ancien (`ORDER BY td.updatedAt DESC`).

## `findAllPublishedPublicWithSteps`
```jpql
SELECT DISTINCT td FROM TravelDiary td
LEFT JOIN FETCH td.media media
WHERE td.isPrivate = FALSE
  AND td.isPublished = TRUE
  AND SIZE(td.steps) > 0
ORDER BY td.updatedAt DESC
```

1. **Sélection** des carnets avec `DISTINCT` pour éviter les doublons.
2. **Jointure** sur `media` pour charger la couverture (même logique que ci-dessus).
3. **Visibilité** : `isPrivate` doit être strictement `false` et `isPublished` vrai.
4. **Contenu** : `SIZE(td.steps) > 0` garantit au moins une étape, évitant d'afficher des carnets vides.
5. **Tri** par `updatedAt` décroissant. Cette requête alimente `GET /travel-diaries`.

## `findAllByUserIdOrderByUpdatedAtDesc`
Méthode dérivée Spring Data annotée `@EntityGraph(attributePaths = "media")` pour récupérer la couverture du carnet dans la même requête.

1. **Filtre propriétaire** : ne conserve que les carnets de l'utilisateur ciblé.
2. **Visibilité** : dans la pratique, cette requête est invoquée lorsque le visiteur est le propriétaire ou un admin (voir `TravelDiaryService#getTravelDiariesForUser`). Aucun filtre de publication n'est appliqué afin de remonter les brouillons/privés à ces profils autorisés.
3. **Fetch** via `@EntityGraph` pour charger la couverture en même temps.
4. **Tri** par date de mise à jour décroissante implicite dans la signature.

## `findPublishedPublicByUserId`
```jpql
SELECT DISTINCT td FROM TravelDiary td
LEFT JOIN FETCH td.media media
WHERE td.user.id = :userId
  AND td.isPrivate = FALSE
  AND td.isPublished = TRUE
  AND SIZE(td.steps) > 0
ORDER BY td.updatedAt DESC
```

1. **Filtre utilisateur** : même point de départ que la requête précédente.
2. **Visibilité** : seuls les carnets non privés et publiés sont conservés.
3. **Contenu** : `SIZE(td.steps) > 0` garantit au moins une étape.
4. **Jointure** `FETCH` et **tri** identiques aux autres requêtes. C’est la requête déclenchée lorsqu’un visiteur consulte les carnets d’un autre utilisateur.

## À propos du `SELECT 1`
L’ancienne version utilisait `EXISTS (SELECT 1 FROM Step s WHERE s.travelDiary = td)`. Le `1` est une constante sans importance ; `EXISTS` vérifie simplement qu’au moins une ligne satisfait la condition. Nous avons remplacé cette forme par `SIZE(td.steps) > 0`, plus lisible et reposant sur l’opérateur JPQL.

## Résumé des règles métier
- À la création/mise à jour, un carnet sans étape est automatiquement marqué `isPublished = false` et `isPrivate` est forcé à `false` par défaut (le propriétaire peut ensuite le passer à `true`).
- Les visiteurs et autres utilisateurs ne voient que les carnets publics, publiés et avec étapes.
- Le propriétaire authentifié voit l'intégralité de ses carnets via `/travel-diaries/users/{userId}`.
