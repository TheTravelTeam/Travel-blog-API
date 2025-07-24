package com.wcs.travel_blog.user.service;

import com.wcs.travel_blog.exception.ResourceNotFoundException;
import com.wcs.travel_blog.user.dto.UpsertUserDTO;
import com.wcs.travel_blog.user.dto.UserDTO;
import com.wcs.travel_blog.user.mapper.UserMapper;
import com.wcs.travel_blog.user.model.User;
import com.wcs.travel_blog.user.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, UserMapper userMapper, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
    }

    public List<UserDTO> getAllUsers(){
        List<User> users = userRepository.findAll();
        if(users.isEmpty()){
            throw new ResourceNotFoundException("Aucun utilisateur trouvé");
        }
        return users.stream().map(userMapper::converToDto).collect(Collectors.toList());
    }

    public UserDTO getUserById(Long userId){
        User user = userRepository.findById(userId).orElseThrow(()-> new ResourceNotFoundException("user non trouvé avec l'id : " + userId));
        return userMapper.converToDto(user);
    }

    public UserDTO getUserByEmail(String userEmail){
        User user = userRepository.findByEmail(userEmail).orElseThrow(()-> new ResourceNotFoundException("user non trouvé avec l'email : " + userEmail));
        return userMapper.converToDto(user);
    }

    public UserDTO getUserByUsername(String username){
        User user = userRepository.findByUsername(username).orElseThrow(()-> new ResourceNotFoundException("user non trouvé avec l'username : " + username));
        return userMapper.converToDto(user);
    }

    public UserDTO updateUser(Long userId, UpsertUserDTO upsertUserDTO){
        User existingUser = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("user non trouvé avec l'id : " + userId));

        existingUser.setUpdatedAt(LocalDateTime.now());
        existingUser.setUsername(upsertUserDTO.getUsername());
        existingUser.setEmail(upsertUserDTO.getEmail());
        existingUser.setBiography(upsertUserDTO.getBiography());
        existingUser.setAvatar(upsertUserDTO.getAvatar());
        existingUser.setStatus(upsertUserDTO.getStatus());
        if(upsertUserDTO.getPassword() != null && !upsertUserDTO.getPassword().isBlank()){
            existingUser.setPassword(passwordEncoder.encode(upsertUserDTO.getPassword()));
        }

        return userMapper.converToDto(userRepository.save(existingUser));
    }


    public void deleteUserById(Long userId){
        User user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("Aucun utilisateur trouvé avec l'id : " + userId));
        userRepository.delete(user);
    }



//        User user = userMapper.converToEntity(upsertUserDTO);
//        user.setCreatedAt(LocalDateTime.now());
//        user.setUpdatedAt(LocalDateTime.now());
//        user.setStatus(UserStatus.ACTIVE);
//        user.setRoles(List.of(Role.USER));

}
