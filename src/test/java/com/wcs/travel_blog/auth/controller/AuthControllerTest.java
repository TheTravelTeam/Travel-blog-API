package com.wcs.travel_blog.auth.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wcs.travel_blog.auth.dto.UserRegistrationDTO;
import com.wcs.travel_blog.auth.service.AuthService;
import com.wcs.travel_blog.auth.service.PasswordResetService;
import com.wcs.travel_blog.security.JWTAuthenticationFilter;
import com.wcs.travel_blog.user.dto.UserDTO;
import com.wcs.travel_blog.user.service.UserService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@ActiveProfiles("test")
@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false) // <-- indispensable ici
public class AuthControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AuthService authService;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private PasswordResetService passwordResetService;

    @MockitoBean
    private JWTAuthenticationFilter jwtAuthenticationFilter;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldRegisterUserAndReturn201() throws Exception {
        UserRegistrationDTO userRegistrationDTO = new UserRegistrationDTO();
        userRegistrationDTO.setEmail("test@example.com");
        userRegistrationDTO.setPassword("Password123!");
        userRegistrationDTO.setPseudo("TestUser");

        UserDTO responseDto = new UserDTO();
        responseDto.setId(1L);
        responseDto.setEmail("test@example.com");
        responseDto.setPseudo("TestUser");

        // Configure le mock du service pour qu’il renvoie un UserDTO lorsqu’on appelle registerUser(...).
        // Mockito.any(...) : accepte n’importe quelle instance de UserRegistrationDTO (même si c’est une nouvelle instance).
        // Mockito.eq(...) : exige une égalité stricte de contenu pour le Set des rôles (ici : {"ROLE_USER"}).

        // Pourquoi utiliser Mockito.any(...) ici ?
        // Lorsque MockMvc exécute la requête POST, il désérialise le JSON en un **nouvel objet UserRegistrationDTO**.
        // Même si cet objet contient les **mêmes valeurs** que celui que tu as créé dans ton test,
        // ce n’est **pas la même instance** en mémoire → donc Mockito ne le reconnaît pas avec un .when(...).

        // Résultat sans Mockito.any(...) : le stub ne match pas → registerUser(...) retourne null → réponse vide → test échoue.

        // Solution : on utilise Mockito.any(UserRegistrationDTO.class) pour dire :
        // « Peu importe l'instance exacte, tant que c’est un UserRegistrationDTO, retourne le DTO simulé. »
        when(authService.registerUser(Mockito.any(UserRegistrationDTO.class), Mockito.anySet()))
                .thenReturn(responseDto);
        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userRegistrationDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.email").value("test@example.com"));
    }

    @Test
    void shouldReturn400WhenPasswordIsTooShort() throws Exception {
        UserRegistrationDTO dto = new UserRegistrationDTO();
        dto.setEmail("test@example.com");
        dto.setPseudo("Michel");
        dto.setPassword("123"); // Trop court

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.password").exists());
    }


    @Test
    void shouldReturn400WhenEmailOrUsernameAreMissing() throws Exception {
        UserRegistrationDTO dto = new UserRegistrationDTO();
        dto.setPassword("123456789");

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.email").exists())
                .andExpect(jsonPath("$.errors.pseudo").exists());
    }

    @Test
    void shouldReturn401WhenGettingCurrentUserWithoutAuth() throws Exception {
        mockMvc.perform(get("/auth/me"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldReturnCurrentUserWhenAuthenticated() throws Exception {
        UserDTO mockUser = new UserDTO();
        mockUser.setId(1L);
        mockUser.setEmail("test@example.com");
        mockUser.setPseudo("TestUser");

        Authentication auth = Mockito.mock(Authentication.class);
        when(auth.isAuthenticated()).thenReturn(true);
        when(auth.getName()).thenReturn("test@example.com");
        when(userService.getUserByEmail("test@example.com")).thenReturn(mockUser);

        mockMvc.perform(get("/auth/me").principal(auth))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.email").value("test@example.com"));
    }

    @Test
    void shouldTriggerPasswordResetAndReturn204() throws Exception {
        String payload = "{\"token\":\"abc-123\",\"password\":\"Password42!\"}";

        mockMvc.perform(post("/auth/reset-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isNoContent());

        Mockito.verify(passwordResetService)
                .resetPassword("abc-123", "Password42!");
    }

}
