package com.wcs.travel_blog.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wcs.travel_blog.exception.ResourceNotFoundException;
import com.wcs.travel_blog.security.JWTAuthenticationFilter;
import com.wcs.travel_blog.security.JWTService;
import com.wcs.travel_blog.user.dto.UpsertUserDTO;
import com.wcs.travel_blog.user.dto.UserDTO;
import com.wcs.travel_blog.user.dto.UserWithDiariesDTO;
import com.wcs.travel_blog.user.service.UserService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

//Test d'intégration user
@ActiveProfiles("test")
@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false) // <-- indispensable ici
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private JWTAuthenticationFilter jwtAuthenticationFilter;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void getAllUsers_shouldReturnCodeStatus200() throws Exception {
        //Arrange
        UserDTO userDTO1 = new UserDTO();
        userDTO1.setId(1L);
        userDTO1.setPseudo("user1");

        UserDTO userDTO2 = new UserDTO();
        userDTO2.setId(2L);
        userDTO2.setPseudo("user2");

        when(userService.getAllUsers()).thenReturn(List.of(userDTO1, userDTO2));

        // Atc & Assert
        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].pseudo").value("user1"))
                .andExpect(jsonPath("$[1].pseudo").value("user2"));
    }

    @Test
    void getUserById_shouldReturnExistingUser() throws Exception {
        //Arrange
        UserWithDiariesDTO userDTO1 = new UserWithDiariesDTO();
        userDTO1.setId(1L);
        userDTO1.setPseudo("user1");

        when(userService.getUserById(1L)).thenReturn(userDTO1);

        // Act & assert
        mockMvc.perform(get("/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.pseudo").value("user1"));
    }


    @Test
    void getUserByPseudo_shouldReturnUser() throws Exception {
        UserWithDiariesDTO dto = new UserWithDiariesDTO();
        dto.setId(1L);
        dto.setPseudo("user1");

        when(userService.getUserByPseudo("user1")).thenReturn(dto);

        mockMvc.perform(get("/users/pseudo?pseudo=user1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.pseudo").value("user1"));
    }

    @Test
    void getUserByEmail_shouldReturnUser() throws Exception {
        UserDTO dto = new UserDTO();
        dto.setId(2L);
        dto.setEmail("user1@gmail.com");

        when(userService.getUserByEmail("user1@gmail.com")).thenReturn(dto);

        mockMvc.perform(get("/users/email?email=user1@gmail.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("user1@gmail.com"));
    }

    @Test
    void updateUser_shouldReturnUpdatedUser() throws Exception {
        UpsertUserDTO updateDTO = new UpsertUserDTO();
        updateDTO.setPseudo("updatedUser");
        updateDTO.setEmail("updated@gmail.com");
        updateDTO.setBiography("new bio");
        updateDTO.setAvatar("https://avatar.png");
        updateDTO.setPassword("newPassword123");

        UserDTO updatedDTO = new UserDTO();
        updatedDTO.setId(1L);
        updatedDTO.setPseudo("updatedUser");
        updatedDTO.setEmail("updated@gmail.com");
        updatedDTO.setBiography("new bio");
        updatedDTO.setAvatar("https://avatar.png");

        when(userService.updateUser(Mockito.eq(1L), Mockito.any(UpsertUserDTO.class)))
                .thenReturn(updatedDTO);

        mockMvc.perform(put("/users/1")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO))) // simule l'objet java en JSON pour le test coverti le DTO en { username: "" , ...}
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.pseudo").value("updatedUser"))
                .andExpect(jsonPath("$.email").value("updated@gmail.com"));
    }


    @Test
    void getUserById_shouldReturnNotFoundException() throws Exception {
        //Arrange
        when(userService.getUserById(99L)).thenThrow(new ResourceNotFoundException("Aucun user trouvé"));

        // Act & assert
        mockMvc.perform(get("/users/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteUserById_shouldReturnNoContent() throws Exception {
        mockMvc.perform(delete("/users/1"))
                .andExpect(status().isOk())
                .andExpect(content().string("Utilisateur supprimé avec succès."));

        Mockito.verify(userService).deleteUserById(1L);
    }
}
