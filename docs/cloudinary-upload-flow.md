# Cloudinary Upload Flow

## Vue d’ensemble
Le module Cloudinary gère désormais l’upload complet côté serveur, enregistre les fichiers dans la base et génère des URL transformées. Le contrôleur expose quatre routes : upload multipart, obtention optionnelle de signature, enregistrement d’un média et lecture d’une URL.

## Configuration requise
- Variables d’environnement (`.env`) : `CLOUDINARY_CLOUD_NAME`, `CLOUDINARY_API_KEY`, `CLOUDINARY_API_SECRET`, `CLOUDINARY_UPLOAD_PRESET`.
- `CloudinaryConfig` construit un client `Cloudinary` à partir de ces valeurs et force l’utilisation d’URL sécurisées.
- Les requêtes aux endpoints doivent être authentifiées (JWT) car `@PreAuthorize("isAuthenticated()")` est présent.

## Étape 1 — Upload multipart côté backend (`POST /cloudinary/upload`)
1. Le front envoie une requête `multipart/form-data` contenant :
   - `file` (obligatoire),
   - `folder?`, `publicId?`, `resourceType?` en champs additionnels.
2. `CloudinaryController.uploadMedia` vérifie l’authentification, récupère les paramètres puis délègue à `CloudinaryService.uploadFile`.
3. `CloudinaryService.uploadFile` :
   - valide le fichier (non vide),
   - construit une map d’options (`folder`, `public_id`, `resource_type`),
   - utilise `cloudinary.uploader().upload(file.getBytes(), options)` pour transférer le binaire,
   - vérifie la présence de `public_id` et `secure_url` dans la réponse Cloudinary.
4. La réponse renvoie `CloudinaryUploadResponse { publicId, secureUrl, resourceType }`, immédiatement exploitable côté front.

## Étape 2 — Signature optionnelle (`POST /cloudinary/signature`)
Cette route reste disponible pour :
- les scripts externes ou automatisations nécessitant une signature côté serveur,
- des clients legacy qui continuent à uploader directement vers l’API Cloudinary.

Le fonctionnement est identique à l’ancienne implémentation : calcul d’un `timestamp`, ajout des paramètres pertinents et signature via `cloudinary.apiSignRequest(...)`. La réponse contient `timestamp`, `signature`, `apiKey`, `cloudName`, `uploadPreset`.

## Étape 3 — Enregistrement de l’asset (`POST /cloudinary/media`)
1. Le front envoie `CloudinaryAssetRequest` : `publicId`, `secureUrl`, `mediaType?`, `isVisible?`, `stepId?`, `travelDiaryId?`, `articleId?`.
2. `CloudinaryController` vérifie à nouveau l’authentification puis délègue à `MediaService.saveFromCloudinary`.
3. `MediaService` :
   - Réutilise un média existant si `publicId` est déjà connu, sinon crée une nouvelle entité.
   - Définit `mediaType` sur `MediaType.PHOTO` si absent, `isVisible` sur `true` par défaut.
   - Associe l’asset à un `Step`, `TravelDiary` ou `Article` après vérification d’existence (sinon 404).
   - Met à jour `createdAt`/`updatedAt` et persiste via `MediaRepository`.
4. La réponse renvoie le `MediaDTO` complet, prêt à être affiché ou stocké côté front.

## Étape 4 — Récupération d’une URL Cloudinary (`GET /cloudinary/media/{publicId}`)
- Vérifie d’abord que l’asset existe en base.
- Construit une URL sécurisée via `CloudinaryService.buildDeliveryUrl` en appliquant, si demandés, `width`, `height`, `crop`, `quality`, `format` (paramètres query).
- Retourne `CloudinaryUrlResponse { publicId, url }`.

## Gestion des erreurs courantes
- **Identifiants manquants** : l’upload échoue côté Cloudinary si la configuration est absente.
- **Lien vers entité inexistante** : 404 (`step`, `travelDiary`, `article` non trouvé).
- **Upload Cloudinary échoué** : renvoyé sous forme de `502 Bad Gateway` lorsque Cloudinary signale une erreur (signature invalide, quota dépassé, format rejeté…).
