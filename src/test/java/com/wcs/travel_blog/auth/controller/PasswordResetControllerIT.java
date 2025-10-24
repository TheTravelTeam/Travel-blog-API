package com.wcs.travel_blog.auth.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wcs.travel_blog.auth.dto.PasswordResetRequestDTO;
import com.wcs.travel_blog.auth.model.PasswordResetToken;
import com.wcs.travel_blog.auth.repository.PasswordResetTokenRepository;
import com.wcs.travel_blog.user.model.User;
import com.wcs.travel_blog.user.repository.UserRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
class PasswordResetControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordResetTokenRepository passwordResetTokenRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private EntityManager entityManager;

    @BeforeEach
    void setUp() {
        passwordResetTokenRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void shouldResetPasswordWhenTokenValid() throws Exception {
        User user = buildUser("alice@example.com", "OldPass123!");
        userRepository.save(user);

        PasswordResetToken token = new PasswordResetToken(
                "token-123",
                user,
                LocalDateTime.now().plusMinutes(30),
                LocalDateTime.now()
        );
        passwordResetTokenRepository.save(token);

        PasswordResetRequestDTO request = new PasswordResetRequestDTO();
        request.setToken("token-123");
        request.setPassword("NewPass123!");

        mockMvc.perform(post("/auth/reset-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNoContent());

        User reloaded = userRepository.findByEmail("alice@example.com").orElseThrow();
        assertThat(passwordEncoder.matches("NewPass123!", reloaded.getPassword())).isTrue();
        assertThat(passwordResetTokenRepository.findByToken("token-123")).isEmpty();
    }

    @Test
    void shouldReturn400WhenTokenUnknown() throws Exception {
        PasswordResetRequestDTO request = new PasswordResetRequestDTO();
        request.setToken("unknown");
        request.setPassword("NewPass123!");

        mockMvc.perform(post("/auth/reset-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturn400WhenTokenExpired() throws Exception {
        User user = buildUser("bob@example.com", "OldPass123!");
        userRepository.save(user);

        PasswordResetToken token = new PasswordResetToken(
                "expired-token",
                user,
                LocalDateTime.now().minusMinutes(1),
                LocalDateTime.now().minusMinutes(10)
        );
        passwordResetTokenRepository.save(token);

        PasswordResetRequestDTO request = new PasswordResetRequestDTO();
        request.setToken("expired-token");
        request.setPassword("NewPass123!");

        mockMvc.perform(post("/auth/reset-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        assertThat(passwordResetTokenRepository.findByToken("expired-token")).isEmpty();
    }

    private User buildUser(String email, String rawPassword) {
        User user = new User();
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(rawPassword));
        user.setPseudo("tester");
        user.setRoles(Set.of("ROLE_USER"));
        return user;
    }
}
