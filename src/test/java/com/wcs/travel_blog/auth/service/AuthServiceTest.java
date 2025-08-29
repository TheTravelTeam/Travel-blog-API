package com.wcs.travel_blog.auth.service;

import com.wcs.travel_blog.auth.dto.UserRegistrationDTO;
import com.wcs.travel_blog.exception.EmailAlreadyExistException;
import com.wcs.travel_blog.user.dto.UserDTO;
import com.wcs.travel_blog.user.mapper.UserMapper;
import com.wcs.travel_blog.user.model.User;
import com.wcs.travel_blog.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private AuthService authService;

    @Mock
    private PasswordEncoder passwordEncoder;

    UserRegistrationDTO getUserRegistrationDTO(){
        UserRegistrationDTO userRegistrationDTO = new UserRegistrationDTO();
        userRegistrationDTO.setEmail("test@example.com");
        userRegistrationDTO.setPassword("password123");
        userRegistrationDTO.setPseudo("TestUser");
        return userRegistrationDTO;
    }

    User getNewUserEntity(){
        User newUserEntity = new User();
        newUserEntity.setEmail("test@example.com");
        newUserEntity.setPassword("password123");
        newUserEntity.setPseudo("TestUser");
        newUserEntity.setRoles(Set.of("ROLE_USER"));
        return newUserEntity;
    }

    UserDTO getUserDTOResponse(){
        UserDTO expectedDto = new UserDTO();
        expectedDto.setId(1L);
        expectedDto.setEmail("test@example.com");
        expectedDto.setPseudo("TestUser");
        expectedDto.setRoles(Set.of("ROLE_USER"));
        expectedDto.setStatus("ACTIVE");
        expectedDto.setCreatedAt(LocalDateTime.now());
        expectedDto.setUpdatedAt(LocalDateTime.now());
        expectedDto.setAvatar(null);
        expectedDto.setBiography(null);
        return expectedDto;
    }
    @Test
    void shouldRegisterUserSuccessfully() {
        // Arrange
        UserRegistrationDTO userRegistrationDTO = getUserRegistrationDTO();

        User newUserEntity = getNewUserEntity();

        User savedUser = newUserEntity;
        savedUser.setId(1L);

        UserDTO expectedDto = getUserDTOResponse();

        when(userMapper.converToEntityOnCreate(userRegistrationDTO, Set.of("ROLE_USER"))).thenReturn(newUserEntity);
        when(userRepository.existsByEmail(newUserEntity.getEmail())).thenReturn(false);
        when(passwordEncoder.encode(newUserEntity.getPassword())).thenReturn("EncodedPassword");
        when(userRepository.save(newUserEntity)).thenReturn(savedUser);
        when(userMapper.converToDto(savedUser)).thenReturn(expectedDto);

        // Act
        UserDTO result = authService.registerUser(userRegistrationDTO, Set.of("ROLE_USER"));

        // Asserts
        assertThat("test@example.com").isEqualTo(result.getEmail());
        assertThat("TestUser").isEqualTo(result.getPseudo());
        assertThat(Set.of("ROLE_USER")).isEqualTo(result.getRoles());
        assertThat(newUserEntity.getPassword()).isEqualTo("EncodedPassword");
        assertThat(1L).isEqualTo(result.getId());
        verify(passwordEncoder).encode("password123");
        verify(userRepository).save(newUserEntity);
        verify(userMapper).converToDto(savedUser);
    }


    @Test
    void shouldThrowErrorWhenEmailAlreadyExists() {
        UserRegistrationDTO userRegistrationDTO = getUserRegistrationDTO();

        User user = getNewUserEntity();

        when(userMapper.converToEntityOnCreate(userRegistrationDTO, Set.of("ROLE_USER"))).thenReturn(user);
        when(userRepository.existsByEmail(user.getEmail())).thenReturn(true);


        assertThatThrownBy(() -> authService.registerUser(userRegistrationDTO, Set.of("ROLE_USER")))
                .isInstanceOf(EmailAlreadyExistException.class)
                .hasMessage("Cet email est déjà utilisé");

        // Vérifie que la méthode save n'a jamais été appelée sur le mock userRepository
        verify(userRepository, never()).save(any());
    }


    @Test
    void shouldEncodePasswordBeforeSaving() {
        UserRegistrationDTO userRegistrationDTO = getUserRegistrationDTO();

        User newUserEntity = getNewUserEntity();

        User savedUser = newUserEntity;
        savedUser.setId(42L);

        when(userMapper.converToEntityOnCreate(userRegistrationDTO, Set.of("ROLE_USER"))).thenReturn(newUserEntity);
        when(userRepository.existsByEmail(userRegistrationDTO.getEmail())).thenReturn(false);
        when(passwordEncoder.encode(newUserEntity.getPassword())).thenReturn("encodedPW");
        when(userRepository.save(newUserEntity)).thenReturn(savedUser);
        when(userMapper.converToDto(savedUser)).thenReturn(new UserDTO());

        authService.registerUser(userRegistrationDTO, Set.of("ROLE_USER"));

        verify(passwordEncoder).encode("password123");
        assertThat(newUserEntity.getPassword()).isEqualTo("encodedPW");
    }
}
