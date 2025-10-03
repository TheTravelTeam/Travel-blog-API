package com.wcs.travel_blog.user.service;

import com.wcs.travel_blog.exception.ResourceNotFoundException;
import com.wcs.travel_blog.step.model.Step;
import com.wcs.travel_blog.travel_diary.model.TravelDiary;
import com.wcs.travel_blog.travel_diary.model.TravelStatus;
import com.wcs.travel_blog.user.dto.UpsertUserDTO;
import com.wcs.travel_blog.user.dto.UserDTO;
import com.wcs.travel_blog.user.dto.UserWithDiariesDTO;
import com.wcs.travel_blog.user.mapper.UserMapper;
import com.wcs.travel_blog.user.model.User;
import com.wcs.travel_blog.user.model.UserStatus;
import com.wcs.travel_blog.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

//Test unitaires user
@ActiveProfiles("test")
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
        user1.setPseudo("user1");
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
        user2.setPseudo("user2");
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
        userDTO1.setPseudo("user1");
        userDTO1.setEmail("user1@gmail.com");
        userDTO1.setAvatar("https://example.com/user1.png");
        userDTO1.setBiography("user1 biography with a length of 100 characters");

        return userDTO1;
    }

    UserDTO getUserDTO2(){
        UserDTO userDTO2 = new UserDTO();
        userDTO2.setId(2L);
        userDTO2.setPseudo("user2");
        userDTO2.setEmail("user2@gmail.com");
        userDTO2.setAvatar("https://example.com/user2.png");
        userDTO2.setBiography("user1 biography with a length of 100 characters");
        return userDTO2;
    }

    UserWithDiariesDTO getUserWithDiariesDTO1(){
        UserWithDiariesDTO userWithDiariesDTO1 = new UserWithDiariesDTO();
        userWithDiariesDTO1.setId(3L);
        userWithDiariesDTO1.setPseudo("user2");
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
        assertThat(users.get(0).getPseudo()).isEqualTo("user1");
        assertThat(users.get(1).getPseudo()).isEqualTo("user2");

    }

    @Test
    void  getUserById_shouldReturnUniqueUser(){
        //Arrange
        User user = getUser1();
        UserWithDiariesDTO userDTO = getUserWithDiariesDTO1();

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userMapper.converToDtoWithDiaries(user, true)).thenReturn(userDTO);

        //Act
        UserWithDiariesDTO result = userService.getUserById(1L, 1L, false);

        //Assert
        assertThat(result.getPseudo()).isEqualTo("user2");
        verify(userMapper).converToDtoWithDiaries(user, true);
    }

    @Test
    void getUserById_shouldFilterDiariesForVisitor() {
        // Arrange
        User user = getUser1();
        UserWithDiariesDTO userDTO = getUserWithDiariesDTO1();

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userMapper.converToDtoWithDiaries(user, false)).thenReturn(userDTO);

        // Act
        UserWithDiariesDTO result = userService.getUserById(1L, 5L, false);

        // Assert
        assertThat(result).isEqualTo(userDTO);
        verify(userMapper).converToDtoWithDiaries(user, false);
    }

    @Test
    void getUserById_shouldReturnAllDiariesForAdmin() {
        User user = getUser1();
        UserWithDiariesDTO userDTO = getUserWithDiariesDTO1();

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userMapper.converToDtoWithDiaries(user, true)).thenReturn(userDTO);

        UserWithDiariesDTO result = userService.getUserById(1L, 5L, true);

        assertThat(result).isEqualTo(userDTO);
        verify(userMapper).converToDtoWithDiaries(user, true);
    }

    @Test
    void getUserById_shouldThrowWhenNotFound(){
        //arrange
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        //act & assert
        assertThatThrownBy(() -> userService.getUserById(99L, null, false))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("user non trouvé avec l'id : 99");
    }

    @Test
    void findByEmail_withoutDiaries_shouldReturnUser() {
        User user = getUser1();
        UserDTO userDTO = getUserDTO1();

        when(userRepository.findByEmail("user1@gmail.com")).thenReturn(Optional.of(user));
        when(userMapper.converToDto(user)).thenReturn(userDTO);

        UserDTO result = userService.getUserByEmail("user1@gmail.com");
        assertThat(result.getEmail()).isEqualTo("user1@gmail.com");
    }

    @Test
    void findByEmail_withDiaries_shouldReturnUser() {
        User user = getUser1();
        UserWithDiariesDTO userDTO = getUserWithDiariesDTO1();

        when(userRepository.findByEmail("user2@gmail.com")).thenReturn(Optional.of(user));
        when(userMapper.converToDtoWithDiaries(user, true)).thenReturn(userDTO);

        UserWithDiariesDTO result = userService.getUserWithDiariesByEmail("user2@gmail.com");
        assertThat(result.getEmail()).isEqualTo("user2@gmail.com");
    }

    @Test
    void findByUsername_shouldReturnUser() {
        User user = getUser1();
        UserWithDiariesDTO userDTO = getUserWithDiariesDTO1();

        when(userRepository.findByPseudo("user2")).thenReturn(Optional.of(user));
        when(userMapper.converToDtoWithDiaries(user)).thenReturn(userDTO);

        UserWithDiariesDTO result = userService.getUserByPseudo("user2");
        assertThat(result.getPseudo()).isEqualTo("user2");
    }

    @Test
    void updateUser_shouldUpdateAndReturnDTO() {
        // Arrange

        // Simulation de l'user find
        User existingUser = getUser1();

        // Objet entrant
        UpsertUserDTO updateData = new UpsertUserDTO();
        updateData.setPseudo("updatedUser");
        updateData.setEmail("updated@gmail.com");
        updateData.setBiography("updated bio");
        updateData.setAvatar("updated-avatar.png");
        updateData.setPassword("newPassword123!");

        // simuler les changements sur l'user Find
        User updatedUser = getUser1();
        updatedUser.setPseudo("updatedUser");
        updatedUser.setEmail("updated@gmail.com");
        updatedUser.setBiography("updated bio");
        updatedUser.setAvatar("updated-avatar.png");
        updatedUser.setPassword("encodedPassword"); // encoder

        // Mapper les changements pour le retour
        UserDTO updatedDTO = getUserDTO1();
        updatedDTO.setPseudo("updatedUser");
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
        assertThat(result.getPseudo()).isEqualTo("updatedUser");
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

    @Test
    void updateUser_shouldDisableContentWhenStatusBecomesBlocked() {
        User existingUser = getUser1();
        existingUser.setStatus(UserStatus.ACTIVE);

        TravelDiary diary = new TravelDiary();
        diary.setStatus(TravelStatus.IN_PROGRESS);
        Step step = new Step();
        step.setStatus(TravelStatus.IN_PROGRESS);
        diary.setSteps(new ArrayList<>(List.of(step)));
        existingUser.setTravel_diaries(new ArrayList<>(List.of(diary)));

        UpsertUserDTO updateData = new UpsertUserDTO();
        updateData.setPseudo(existingUser.getPseudo());
        updateData.setEmail(existingUser.getEmail());
        updateData.setBiography(existingUser.getBiography());
        updateData.setAvatar(existingUser.getAvatar());
        updateData.setStatus(UserStatus.BLOCKED);

        when(userRepository.findById(existingUser.getId())).thenReturn(Optional.of(existingUser));
        when(userRepository.save(existingUser)).thenReturn(existingUser);

        userService.updateUser(existingUser.getId(), updateData);

        assertThat(diary.getStatus()).isEqualTo(TravelStatus.DISABLED);
        assertThat(step.getStatus()).isEqualTo(TravelStatus.DISABLED);
        verify(userRepository).save(existingUser);
    }

    @Test
    void updateUser_shouldRestoreContentWhenStatusBecomesActive() {
        User existingUser = getUser1();
        existingUser.setStatus(UserStatus.BLOCKED);

        TravelDiary completedDiary = new TravelDiary();
        completedDiary.setStatus(TravelStatus.DISABLED);
        completedDiary.setEndDate(LocalDate.now());
        Step completedStep = new Step();
        completedStep.setStatus(TravelStatus.DISABLED);
        completedStep.setStartDate(LocalDate.now());
        completedStep.setEndDate(LocalDate.now());
        completedDiary.setSteps(new ArrayList<>(List.of(completedStep)));

        TravelDiary inProgressDiary = new TravelDiary();
        inProgressDiary.setStatus(TravelStatus.DISABLED);
        Step inProgressStep = new Step();
        inProgressStep.setStatus(TravelStatus.DISABLED);
        inProgressStep.setStartDate(LocalDate.now());
        inProgressDiary.setSteps(new ArrayList<>(List.of(inProgressStep)));

        existingUser.setTravel_diaries(new ArrayList<>(List.of(completedDiary, inProgressDiary)));

        UpsertUserDTO updateData = new UpsertUserDTO();
        updateData.setPseudo(existingUser.getPseudo());
        updateData.setEmail(existingUser.getEmail());
        updateData.setBiography(existingUser.getBiography());
        updateData.setAvatar(existingUser.getAvatar());
        updateData.setStatus(UserStatus.ACTIVE);

        when(userRepository.findById(existingUser.getId())).thenReturn(Optional.of(existingUser));
        when(userRepository.save(existingUser)).thenReturn(existingUser);

        userService.updateUser(existingUser.getId(), updateData);

        assertThat(completedDiary.getStatus()).isEqualTo(TravelStatus.COMPLETED);
        assertThat(completedStep.getStatus()).isEqualTo(TravelStatus.COMPLETED);
        assertThat(inProgressDiary.getStatus()).isEqualTo(TravelStatus.IN_PROGRESS);
        assertThat(inProgressStep.getStatus()).isEqualTo(TravelStatus.IN_PROGRESS);
        verify(userRepository).save(existingUser);
    }
}
