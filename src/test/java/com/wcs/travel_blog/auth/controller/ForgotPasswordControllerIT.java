package com.wcs.travel_blog.auth.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wcs.travel_blog.auth.dto.ForgotPasswordRequestDTO;
import com.wcs.travel_blog.auth.model.PasswordResetToken;
import com.wcs.travel_blog.auth.repository.PasswordResetTokenRepository;
import com.wcs.travel_blog.notification.service.MailService;
import com.wcs.travel_blog.user.model.User;
import com.wcs.travel_blog.user.repository.UserRepository;
import jakarta.persistence.EntityManager;
import org.hibernate.Hibernate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mail.MailSendException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
class ForgotPasswordControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordResetTokenRepository passwordResetTokenRepository;

    @MockitoBean
    private MailService mailService;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        passwordResetTokenRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void shouldReturn204AndPersistTokenWhenEmailExists() throws Exception {
        User user = buildUser("alice@example.com");
        userRepository.saveAndFlush(user);

        ForgotPasswordRequestDTO request = new ForgotPasswordRequestDTO();
        request.setEmail("alice@example.com");

        mockMvc.perform(post("/auth/forgot-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNoContent());

        List<PasswordResetToken> tokens = passwordResetTokenRepository.findAll();
        assertThat(tokens).hasSize(1);

        PasswordResetToken token = passwordResetTokenRepository
                .findByIdWithUser(tokens.getFirst().getId())
                .orElseThrow();

        assertThat(token.getUser().getEmail()).isEqualTo("alice@example.com");
        assertThat(token.getToken()).isNotBlank();

        verify(mailService).send(anyString(), anyString(), anyString());
    }

    @Test
    void shouldReturn204WhenEmailUnknown() throws Exception {
        ForgotPasswordRequestDTO request = new ForgotPasswordRequestDTO();
        request.setEmail("unknown@example.com");

        mockMvc.perform(post("/auth/forgot-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNoContent());

        assertThat(passwordResetTokenRepository.count()).isZero();
        verifyNoInteractions(mailService);
    }

    @Test
    void shouldStillReturn204WhenMailFails() throws Exception {
        User user = buildUser("bob@example.com");
        userRepository.save(user);

        ForgotPasswordRequestDTO request = new ForgotPasswordRequestDTO();
        request.setEmail("bob@example.com");

        doThrow(new MailSendException("smtp down")).when(mailService).send(anyString(), anyString(), anyString());

        mockMvc.perform(post("/auth/forgot-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNoContent());

        assertThat(passwordResetTokenRepository.findAll()).hasSize(1);
        verify(mailService).send(anyString(), anyString(), anyString());
    }

    private User buildUser(String email) {
        User user = new User();
        user.setEmail(email);
        user.setPassword("password123!");
        user.setPseudo("tester");
        user.setRoles(Set.of("ROLE_USER"));
        return user;
    }
}
