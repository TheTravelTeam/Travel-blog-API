package com.wcs.travel_blog.auth.service;

import com.wcs.travel_blog.auth.model.PasswordResetToken;
import com.wcs.travel_blog.auth.repository.PasswordResetTokenRepository;
import com.wcs.travel_blog.notification.service.MailService;
import com.wcs.travel_blog.exception.ExpiredPasswordResetTokenException;
import com.wcs.travel_blog.exception.InvalidPasswordResetTokenException;
import com.wcs.travel_blog.exception.InvalidPasswordResetRequestException;
import com.wcs.travel_blog.exception.ResourceNotFoundException;
import com.wcs.travel_blog.exception.ExternalServiceException;
import com.wcs.travel_blog.user.model.User;
import com.wcs.travel_blog.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class PasswordResetService {

    private static final String SUBJECT = "Réinitialisation de votre mot de passe";

    private final UserRepository userRepository;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final MailService mailService;
    private final PasswordEncoder passwordEncoder;

    @Value("${auth.password-reset.token-ttl-minutes:30}")
    private long tokenTtlMinutes;

    @Value("${app.frontend-base-url}")
    private String frontendBaseUrl;

    /**
     * Generates a single-use reset token, persists it, and attempts to email the reset link.
     */
    @Transactional
    public void requestPasswordReset(String rawEmail) {
        final String email = normalizeEmail(rawEmail);
        if (!StringUtils.hasText(email)) {
            throw new InvalidPasswordResetRequestException("Une adresse e-mail est requise pour réinitialiser un mot de passe");
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Aucun utilisateur trouvé pour cet email"));
        String token = UUID.randomUUID().toString();
        LocalDateTime createdAt = LocalDateTime.now();
        LocalDateTime expiresAt = createdAt.plusMinutes(getPositiveTtlMinutes());

        passwordResetTokenRepository.deleteByUser(user);
        PasswordResetToken passwordResetToken = new PasswordResetToken(token, user, expiresAt, createdAt);
        passwordResetTokenRepository.save(passwordResetToken);

        String resetUrl = buildResetUrl(token);
        String emailBody = buildEmailBody(resetUrl);

        try {
            mailService.send(user.getEmail(), SUBJECT, emailBody);
            log.info("Password reset email sent for user {}", user.getId());
        } catch (MailException exception) {
            throw new ExternalServiceException("L'envoi de l'email de réinitialisation a échoué", exception);
        }
    }

    /**
     * Validates a reset token and updates the linked user password when the token is valid.
     */
    @Transactional
    public void resetPassword(String rawToken, String newPassword) {
        if (!StringUtils.hasText(rawToken)) {
            throw new InvalidPasswordResetTokenException("Token de réinitialisation invalide");
        }

        String token = rawToken.trim();
        PasswordResetToken passwordResetToken = passwordResetTokenRepository.findByToken(token)
                .orElseThrow(() -> new InvalidPasswordResetTokenException("Token de réinitialisation invalide"));

        if (passwordResetToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            passwordResetTokenRepository.delete(passwordResetToken);
            throw new ExpiredPasswordResetTokenException("Token de réinitialisation expiré");
        }

        User user = passwordResetToken.getUser();
        user.setPassword(passwordEncoder.encode(newPassword));
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);

        passwordResetTokenRepository.deleteByUser(user);
        log.info("Password reset completed for user {}", user.getId());
    }

    /**
     * Protects against invalid configuration by ensuring a positive TTL value.
     */
    private long getPositiveTtlMinutes() {
        return tokenTtlMinutes > 0 ? tokenTtlMinutes : 30L;
    }

    /**
     * Builds the frontend reset URL using the configured base path while stripping trailing slashes.
     */
    private String buildResetUrl(String token) {
        String baseUrl = StringUtils.hasText(frontendBaseUrl) ? frontendBaseUrl : "https://travel-blog.cloud";
        String base = StringUtils.trimTrailingCharacter(baseUrl, '/');
        return base + "/reset-password?token=" + token;
    }

    /**
     * Produces the localized email body including the reset link and TTL reminder.
     */
    private String buildEmailBody(String resetUrl) {
        long ttl = getPositiveTtlMinutes();
        return "Bonjour,\n\n" +
                "Vous avez demandé la réinitialisation de votre mot de passe Travel Blog.\n" +
                "Cliquez sur le lien suivant pour définir un nouveau mot de passe :\n" +
                resetUrl + "\n\n" +
                "Ce lien expirera dans " + ttl + " minute" + (ttl > 1 ? "s" : "") + ". Si vous n'êtes pas à l'origine de cette demande, ignorez cet email.\n\n" +
                "L'équipe Travel Blog";
    }

    /**
     * Trims and lowercases the provided email to avoid duplicate lookups caused by case or spacing.
     */
    private String normalizeEmail(String email) {
        return email == null ? null : email.trim().toLowerCase();
    }
}
