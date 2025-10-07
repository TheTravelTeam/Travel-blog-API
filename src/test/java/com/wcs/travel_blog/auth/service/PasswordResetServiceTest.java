package com.wcs.travel_blog.auth.service;

import com.wcs.travel_blog.auth.model.PasswordResetToken;
import com.wcs.travel_blog.auth.repository.PasswordResetTokenRepository;
import com.wcs.travel_blog.notification.service.MailService;
import com.wcs.travel_blog.user.model.User;
import com.wcs.travel_blog.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.MailSendException;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PasswordResetServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordResetTokenRepository passwordResetTokenRepository;

    @Mock
    private MailService mailService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private PasswordResetService passwordResetService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(passwordResetService, "tokenTtlMinutes", 30L);
        ReflectionTestUtils.setField(passwordResetService, "frontendBaseUrl", "https://front.example");
    }

    @Test
    void shouldGenerateTokenAndSendEmailWhenUserExists() {
        User user = new User();
        user.setId(42L);
        user.setEmail("alice@example.com");
        when(userRepository.findByEmail("alice@example.com")).thenReturn(Optional.of(user));

        passwordResetService.requestPasswordReset("alice@example.com");

        verify(passwordResetTokenRepository).deleteByUser(user);
        ArgumentCaptor<PasswordResetToken> captor = ArgumentCaptor.forClass(PasswordResetToken.class);
        verify(passwordResetTokenRepository).save(captor.capture());
        PasswordResetToken savedToken = captor.getValue();

        assertThat(savedToken.getToken()).isNotBlank();
        LocalDateTime createdAt = savedToken.getCreatedAt();
        LocalDateTime expiresAt = savedToken.getExpiresAt();
        assertThat(expiresAt).isEqualTo(createdAt.plusMinutes(30));

        verify(mailService).send(eq("alice@example.com"), anyString(), contains("reset-password?token="));
    }

    @Test
    void shouldNormalizeEmailWhenLookupUser() {
        User user = new User();
        user.setEmail("alice@example.com");
        when(userRepository.findByEmail("alice@example.com")).thenReturn(Optional.of(user));

        passwordResetService.requestPasswordReset("  ALICE@example.com  ");

        verify(userRepository).findByEmail("alice@example.com");
    }

    @Test
    void shouldRejectRequestWhenEmailBlank() {
        assertThatThrownBy(() -> passwordResetService.requestPasswordReset("   "))
                .isInstanceOf(com.wcs.travel_blog.exception.InvalidPasswordResetRequestException.class)
                .hasMessage("Une adresse e-mail est requise pour réinitialiser un mot de passe");

        verifyNoInteractions(userRepository, passwordResetTokenRepository, mailService);
    }

    @Test
    void shouldThrowWhenUserDoesNotExist() {
        when(userRepository.findByEmail("unknown@example.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> passwordResetService.requestPasswordReset("unknown@example.com"))
                .isInstanceOf(com.wcs.travel_blog.exception.ResourceNotFoundException.class)
                .hasMessage("Aucun utilisateur trouvé pour cet email");

        verify(passwordResetTokenRepository, never()).save(any());
        verify(mailService, never()).send(anyString(), anyString(), anyString());
    }

    @Test
    void shouldWrapMailFailureIntoExternalServiceException() {
        User user = new User();
        user.setId(7L);
        user.setEmail("bob@example.com");
        when(userRepository.findByEmail("bob@example.com")).thenReturn(Optional.of(user));
        doThrow(new MailSendException("SMTP down")).when(mailService).send(anyString(), anyString(), anyString());

        assertThatThrownBy(() -> passwordResetService.requestPasswordReset("bob@example.com"))
                .isInstanceOf(com.wcs.travel_blog.exception.ExternalServiceException.class)
                .hasMessage("L'envoi de l'email de réinitialisation a échoué");

        verify(passwordResetTokenRepository).save(any(PasswordResetToken.class));
        verify(mailService).send(eq("bob@example.com"), anyString(), anyString());
    }

    @Test
    void shouldResetPasswordWhenTokenValid() {
        User user = new User();
        user.setId(9L);
        PasswordResetToken token = new PasswordResetToken("token-123", user, LocalDateTime.now().plusMinutes(15), LocalDateTime.now());
        when(passwordResetTokenRepository.findByToken("token-123")).thenReturn(Optional.of(token));
        when(passwordEncoder.encode("NewPass123!"))
                .thenReturn("encodedPass");

        passwordResetService.resetPassword("token-123", "NewPass123!");

        assertThat(user.getPassword()).isEqualTo("encodedPass");
        verify(userRepository).save(user);
        verify(passwordResetTokenRepository).deleteByUser(user);
    }

    @Test
    void shouldThrowWhenTokenUnknown() {
        when(passwordResetTokenRepository.findByToken("unknown")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> passwordResetService.resetPassword("unknown", "NewPass123!"))
                .isInstanceOf(com.wcs.travel_blog.exception.InvalidPasswordResetTokenException.class);

        verify(userRepository, never()).save(any());
    }

    @Test
    void shouldThrowWhenTokenExpired() {
        User user = new User();
        PasswordResetToken token = new PasswordResetToken("token-123", user, LocalDateTime.now().minusMinutes(1), LocalDateTime.now().minusMinutes(2));
        when(passwordResetTokenRepository.findByToken("token-123")).thenReturn(Optional.of(token));

        assertThatThrownBy(() -> passwordResetService.resetPassword("token-123", "NewPass123!"))
                .isInstanceOf(com.wcs.travel_blog.exception.ExpiredPasswordResetTokenException.class);

        verify(passwordResetTokenRepository).delete(token);
        verify(userRepository, never()).save(any());
    }
}
