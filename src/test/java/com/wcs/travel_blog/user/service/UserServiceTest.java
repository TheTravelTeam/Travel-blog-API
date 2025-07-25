package com.wcs.travel_blog.user.service;

import com.wcs.travel_blog.exception.ResourceNotFoundException;
import com.wcs.travel_blog.user.dto.UpsertUserDTO;
import com.wcs.travel_blog.user.dto.UserDTO;
import com.wcs.travel_blog.user.dto.UserWithDiariesDTO;
import com.wcs.travel_blog.user.mapper.UserMapper;
import com.wcs.travel_blog.user.model.User;
import com.wcs.travel_blog.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

//Test unitaires user
@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Mock
    private UserMapper userMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    User getUser1(){
        User user1 = new User();
        user1.setId(1L);
        user1.setUsername("user1");
        user1.setEmail("user1@gmail.com");
        user1.setPassword("user1PasswordWithEnoughLength&Caracters");
        user1.setCreatedAt(null);
        user1.setUpdatedAt(null);
        user1.setAvatar("https://example.com/user1.png");
        user1.setBiography("user1 biography with a length of 100 characters");
        return  user1;
    }

    User getUser2(){
        User user2 = new User();
        user2.setId(1L);
        user2.setUsername("user2");
        user2.setEmail("user2@gmail.com");
        user2.setPassword("user2PasswordWithEnoughLength&Caracters");
        user2.setCreatedAt(null);
        user2.setUpdatedAt(null);
        user2.setAvatar("https://example.com/user2.png");
        user2.setBiography("user2 biography with a length of 100 characters");

        return user2;
    }

    UserDTO getUserDTO1(){
        UserDTO userDTO1 = new UserDTO();
        userDTO1.setId(1L);
        userDTO1.setUsername("user1");
        userDTO1.setEmail("user1@gmail.com");
        userDTO1.setAvatar("https://example.com/user1.png");
        userDTO1.setBiography("user1 biography with a length of 100 characters");

        return userDTO1;
    }

    UserDTO getUserDTO2(){
        UserDTO userDTO2 = new UserDTO();
        userDTO2.setId(2L);
        userDTO2.setUsername("user2");
        userDTO2.setEmail("user2@gmail.com");
        userDTO2.setAvatar("https://example.com/user2.png");
        userDTO2.setBiography("user1 biography with a length of 100 characters");
        return userDTO2;
    }

    UserWithDiariesDTO getUserWithDiariesDTO1(){
        UserWithDiariesDTO userWithDiariesDTO1 = new UserWithDiariesDTO();
        userWithDiariesDTO1.setId(3L);
        userWithDiariesDTO1.setUsername("user2");
        userWithDiariesDTO1.setEmail("user2@gmail.com");
        userWithDiariesDTO1.setAvatar("https://example.com/user2.png");
        userWithDiariesDTO1.setBiography("user1 biography with a length of 100 characters");
        return userWithDiariesDTO1;
    }


    @Test
    void getAllUsers_shouldReturnListOfAllUsers() {
        // Arrange
        User user1 = getUser1();
        User user2 = getUser2();

        UserDTO userDTO1 = getUserDTO1();
        UserDTO userDTO2 = getUserDTO2();

        when(userRepository.findAll()).thenReturn(List.of(user1, user2));
        when(userMapper.converToDto(user1)).thenReturn(userDTO1);
        when(userMapper.converToDto(user2)).thenReturn(userDTO2);

        //Act
        List<UserDTO> users = userService.getAllUsers();

        // Assert
        assertThat(users).hasSize(2);
        assertThat(users.get(0).getUsername()).isEqualTo("user1");
        assertThat(users.get(1).getUsername()).isEqualTo("user2");

    }

    @Test
    void  getUserById_shouldReturnUniqueUser(){
        //Arrange
        User user = getUser1();
        UserWithDiariesDTO userDTO = getUserWithDiariesDTO1();

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userMapper.converToDtoWithDiaries(user)).thenReturn(userDTO);

        //Act
        UserWithDiariesDTO result = userService.getUserById(1L);

        //Assert
        assertThat(result.getUsername()).isEqualTo("user2");
    }

    @Test
    void getUserById_shouldThrowWhenNotFound(){
        //arrange
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        //act & assert
        assertThatThrownBy(() -> userService.getUserById(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("user non trouvé avec l'id : 99");
    }

    @Test
    void findByEmail_shouldReturnUser() {
        User user = getUser1();
        UserWithDiariesDTO userDTO = getUserWithDiariesDTO1();

        when(userRepository.findByEmail("user2@gmail.com")).thenReturn(Optional.of(user));
        when(userMapper.converToDtoWithDiaries(user)).thenReturn(userDTO);

        UserWithDiariesDTO result = userService.getUserByEmail("user2@gmail.com");
        assertThat(result.getEmail()).isEqualTo("user2@gmail.com");
    }

    @Test
    void findByUsername_shouldReturnUser() {
        User user = getUser1();
        UserWithDiariesDTO userDTO = getUserWithDiariesDTO1();

        when(userRepository.findByUsername("user2")).thenReturn(Optional.of(user));
        when(userMapper.converToDtoWithDiaries(user)).thenReturn(userDTO);

        UserWithDiariesDTO result = userService.getUserByUsername("user2");
        assertThat(result.getUsername()).isEqualTo("user2");

    }

    @Test
    void updateUser_shouldUpdateAndReturnDTO() {
        // Arrange

        // Simulation de l'user find
        User existingUser = getUser1();

        // Objet entrant
        UpsertUserDTO updateData = new UpsertUserDTO();
        updateData.setUsername("updatedUser");
        updateData.setEmail("updated@gmail.com");
        updateData.setBiography("updated bio");
        updateData.setAvatar("updated-avatar.png");
        updateData.setPassword("newPassword123!");

        // simuler les changements sur l'user Find
        User updatedUser = getUser1();
        updatedUser.setUsername("updatedUser");
        updatedUser.setEmail("updated@gmail.com");
        updatedUser.setBiography("updated bio");
        updatedUser.setAvatar("updated-avatar.png");
        updatedUser.setPassword("encodedPassword"); // encoder

        // Mapper les changements pour le retour
        UserDTO updatedDTO = getUserDTO1();
        updatedDTO.setUsername("updatedUser");
        updatedDTO.setEmail("updated@gmail.com");
        updatedDTO.setBiography("updated bio");
        updatedDTO.setAvatar("updated-avatar.png");

        when(userRepository.findById(1L)).thenReturn(Optional.of(existingUser));
        when(passwordEncoder.encode("newPassword123!")).thenReturn("encodedPassword");
        when(userRepository.save(existingUser)).thenReturn(updatedUser);
        when(userMapper.converToDto(updatedUser)).thenReturn(updatedDTO);

        // Act
        UserDTO result = userService.updateUser(1L, updateData);

        // Assert
        assertThat(result.getUsername()).isEqualTo("updatedUser");
        assertThat(result.getEmail()).isEqualTo("updated@gmail.com");
    }


    @Test
    void deleteUserById_shouldDeleteUser(){
        //arrange
        User user = getUser1();

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        // act
        userService.deleteUserById(1L);

        // assert & verify
        // Vérifie que la méthode delete du userRepository est bien appelé avec le bon objet user ici
        verify(userRepository).delete(user);
    }
}
