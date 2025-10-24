# Séquence Forgot Password

## Vue d'ensemble
- Endpoint HTTP : `POST /auth/forgot-password`
- Payload : `{ "email": "user@example.com" }` (validation format + required)
- Réponse : `204 No Content` dans tous les cas pour éviter la divulgation d'existence de compte
- Flux : normalisation email → lookup user → purge anciens tokens → création token TTL → envoi mail → journalisation
- Endpoint confirmation : `POST /auth/reset-password`
  - Payload : `{ "token": "uuid", "password": "NewPass123!" }`
  - Contrôles : token présent, mot de passe ≥ 8 caractères, token valide et non expiré, mot de passe ré-encodé via `PasswordEncoder`
  - Réponse : `204 No Content`; suppression des jetons résiduels après succès

## Modèle & Persistance
- `PasswordResetToken` stocké en base (`token`, `user`, `createdAt`, `expiresAt`), relié à `User`
- Repository : `PasswordResetTokenRepository.deleteByUser` supprime les jetons actifs avant sauvegarde du nouveau
- `PasswordResetTokenRepository.findByToken` alimente la validation lors du reset
- TTL configurable via `auth.password-reset.token-ttl-minutes` (fallback 30 min)

## Service
- `PasswordResetService.requestPasswordReset(email)`
  - Nettoie l'email (trim + lowercase)
  - Ignore les emails inconnus en loggant un debug
  - Génère un UUID, calcule l'expiration, persisté en transaction
  - Construit le lien public : `<FRONTEND_BASE_URL>/reset-password?token=...`
  - Envoie un mail texte localisé (FR) via `MailService`
  - Attrape `MailException`, log erreur mais conserve la réponse HTTP 204
- `PasswordResetService.resetPassword(token, password)`
  - Vérifie la présence du token, charge l'entité, supprime les tokens expirés et lève une erreur dédiée si nécessaire
  - Encode le nouveau mot de passe, met à jour `User.updatedAt`, supprime tous les jetons du compte et loggue l’opération

## Notification
- `MailService` (interface) + `MailServiceImpl` utilisant `JavaMailSender`
- Adresse expéditeur optionnelle via `spring.mail.from`
- Hôte/port SMTP paramétrables (`spring.mail.host`, `spring.mail.port`) avec fallback `localhost:1025`, et variables `spring.mail.username/password` pour l’authentification si nécessaire

## Configuration
- `app.frontend-base-url` : URL du front (fallback `https://travel-blog.cloud`)
- `auth.password-reset.token-ttl-minutes` : durée de vie jeton
- Variables échantillon ajoutées à `.env.sample`
- Dépendance `spring-boot-starter-mail` ajoutée au `pom.xml`

## Tests
- Unitaires : `PasswordResetServiceTest` (succès, normalisation, email inconnu, erreur SMTP, reset token invalide/expiré)
- Intégration : `ForgotPasswordControllerIT` (succès, email inconnu, SMTP down) et `PasswordResetControllerIT` (succès, token inconnu, token expiré)
- Profil test (`src/test/resources/application-test.properties`) configure H2 + valeurs par défaut du module

## Points d'attention
- Activer un transport SMTP réel en prod (`spring.mail.*`)
- Fournir l'URL front correspondante à l'environnement
- Côté front, consommer `/reset-password` pour saisir le nouveau mot de passe et propager le token
