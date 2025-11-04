# Code Simplification Notes

Ce document récapitule les allégements apportés au code pour le rendre plus lisible par une équipe junior.

1. **Sécurité HTTP**
   - `SecurityConfig` n’utilise plus de regex ni d’injection de service custom.
   - La configuration CORS lit directement la variable `cors.allowed-origin` sans méthode utilitaire séparée et prévoit simplement un fallback.

2. **Module Cloudinary**
   - Le contrôleur injecte directement `CloudinaryService` et `MediaService`; suppression du `RateLimiterService`, du `FeatureDisabledException` et des logs détaillés.
   - `CloudinaryService` n’est plus conditionnel : il se contente de signer et de générer des URL.
   - `CloudinaryConfig` instancie toujours `Cloudinary` avec les identifiants présents dans `.env`.

3. **Gestion des médias**
   - `MediaService` réutilise une méthode privée pour lier `Step`, `TravelDiary` et `Article`, réduisant les répétitions dans `updateMedia` et `saveFromCloudinary`.
   - Conversion des listes via `.toList()` pour éviter les collecteurs verbeux.
   - L’entité `Media` ne contient plus de commentaires longs sur JPA, ce qui la rend plus simple à parcourir.

4. **Gestion des erreurs**
   - `GlobalExceptionHandler` regroupe seulement les cas utiles (404, 401, 403, 409, 400 validation) et laisse le reste au handler global.

5. **Nettoyage**
   - Suppression des classes `RateLimiterService`, `FeatureDisabledException` et `TooManyRequestsException` devenues inutiles.
   - Mise à jour de `docs/cloudinary-upload-flow.md` pour refléter le flux simplifié.
