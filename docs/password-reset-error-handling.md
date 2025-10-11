# Gestion des erreurs de réinitialisation de mot de passe

Ce document résume la manière dont le service de réinitialisation de mot de passe gère désormais les erreurs côté backend.

## `PasswordResetService.requestPasswordReset`
- Vérifie que l'email fourni est non vide. Si la valeur est absente ou uniquement composée d'espaces, `InvalidPasswordResetRequestException` est levée et renvoyée au client avec un statut HTTP 400.
- Recherche l'utilisateur via `UserRepository`. Si l'email n'existe pas, `ResourceNotFoundException` est levée, ce qui produit une réponse HTTP 404.
- En cas d'échec lors de l'envoi du courriel (exception `MailException`), l'erreur est transformée en `ExternalServiceException`. La couche REST renvoie alors un statut HTTP 502.
- Le `@Transactional` garantit que la suppression de jetons existants, la création du nouveau jeton et sa persistance sont atomiques : si l'une de ces opérations échoue, l'ensemble est annulé pour éviter un état incohérent.

## `PasswordResetService.resetPassword`
- Valide le token fourni et renvoie `InvalidPasswordResetTokenException` (HTTP 400) si le jeton est absent ou inconnu.
- Supprime et signale les jetons expirés via `ExpiredPasswordResetTokenException` (HTTP 400).
- Encode le nouveau mot de passe et nettoie les jetons restants lorsque tout est valide.

## Gestion globale des exceptions
- Les exceptions ci-dessus sont gérées par `GlobalExceptionHandler`, ce qui assure des réponses HTTP cohérentes et localisées.
- Aucun log n'est utilisé pour signaler des erreurs de validation ou d'accès à des ressources inexistantes ; les erreurs sont propagées sous forme d'exceptions pour laisser la responsabilité de la réponse au contrôleur HTTP.
