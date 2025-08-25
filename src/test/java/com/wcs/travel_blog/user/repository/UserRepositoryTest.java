package com.wcs.travel_blog.user.repository;


import com.wcs.travel_blog.user.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;


@DataJpaTest
public class UserRepositoryTest {

    // @DataJpaTest démarre un contexte Spring limité aux composants liés à JPA (repositories, datasource, EntityManager, etc.) ;
    // et fournit automatiquement des beans Spring réels, pas des mocks, pour les tests d’intégration JPA.
    // donc : UserRepository est un bean Spring déclaré via Spring Data JPA
    // Donc pour l’utiliser dans ton test, on doit : soit le demander à Spring via @Autowired, soit injection via le constructeur
    @Autowired
    private UserRepository userRepository;

    User getUser1(){
        User user1 = new User();
        user1.setUsername("user1");
        user1.setEmail("user1@gmail.com");
        user1.setPassword("user1PasswordWithEnoughLength&Caracters");
        user1.setAvatar("https://example.com/user1.png");
        user1.setBiography("user1 biography with a length of 100 characters");
        return  user1;
    }

    User getUser2(){
        User user2 = new User();
        user2.setUsername("user2");
        user2.setEmail("user2@gmail.com");
        user2.setPassword("user2PasswordWithEnoughLength&Caracters");
        user2.setAvatar("https://example.com/user2.png");
        user2.setBiography("user2 biography with a length of 100 characters");

        return user2;
    }

    @Test
    void testGetAllUsers_shouldReturnListOfAllUsers() {
        // Arrange :  créer et sauvegarder des users dans la base de données
        User user1 = getUser1();
        User user2 = getUser2();

        userRepository.saveAll(List.of(user1, user2));

        // Act : récupérer toutes les users via findAll()
        List<User> users = userRepository.findAll();

        // Assert : vérifier que la liste retournée contient bien les users
        assertThat(users).hasSize(2);
        assertThat(users.get(0).getUsername()).isEqualTo("user1");
        assertThat(users.get(1).getUsername()).isEqualTo("user2");
    }

    @Test
    void testFindByEmail_shouldReturnUser() {
        // Arrange
        User user = getUser1();
        userRepository.save(user);

        // Act
        Optional<User> result = userRepository.findByEmail("user1@gmail.com");

        // Assert
        assertThat(result).isPresent();
        assertThat(result.get().getUsername()).isEqualTo("user1");
    }

    @Test
    void testFindByEmail_shouldReturnEmpty() {
        // Act
        Optional<User> result = userRepository.findByEmail("nonexistent@email.com");

        // Assert
        assertThat(result).isEmpty();
    }

    @Test
    void testFindByUsername_shouldReturnUser() {
        // Arrange
        User user = getUser2();
        userRepository.save(user);

        // Act
        Optional<User> result = userRepository.findByUsername("user2");

        // Assert
        assertThat(result).isPresent();
        assertThat(result.get().getEmail()).isEqualTo("user2@gmail.com");
    }

    @Test
    void testFindByUsername_shouldReturnEmpty() {
        // Act
        Optional<User> result = userRepository.findByUsername("unknownUser");

        // Assert
        assertThat(result).isEmpty();
    }

    @Test
    void testExistsByEmail_shouldReturnTrue() {
        // Arrange
        User user = getUser1();
        userRepository.save(user);

        // Act
        boolean exists = userRepository.existsByEmail("user1@gmail.com");

        // Assert
        assertThat(exists).isTrue();
    }

    @Test
    void testExistsByEmail_shouldReturnFalse() {
        // Act
        boolean exists = userRepository.existsByEmail("doesnotexist@email.com");

        // Assert
        assertThat(exists).isFalse();
    }

    @Test
    void testDeleteUser_shouldRemoveUserFromDatabase() {
        // Arrange
        User user = getUser1();
        User savedUser = userRepository.save(user);

        // Vérification que l'utilisateur est bien présent
        assertThat(userRepository.findById(savedUser.getId())).isPresent();

        // Act
        userRepository.delete(savedUser);

        // Assert
        Optional<User> result = userRepository.findById(savedUser.getId());
        assertThat(result).isEmpty(); // L'
    }

    @Test
    void testDeleteUser_shouldDoNothingIfUserNotExists() {
        // Arrange
        User nonExistentUser = getUser2(); // jamais persisté en BDD

        // Act & Assert
        // On vérifie simplement qu’aucune exception n’est lancée
        userRepository.delete(nonExistentUser);

        // Et que la base est toujours vide
        List<User> users = userRepository.findAll();
        assertThat(users).isEmpty();
    }



}
