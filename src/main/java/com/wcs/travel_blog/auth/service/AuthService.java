package com.wcs.travel_blog.auth.service;

import com.wcs.travel_blog.auth.dto.UserRegistrationDTO;
import com.wcs.travel_blog.exception.EmailAlreadyExistException;
import com.wcs.travel_blog.user.dto.UserDTO;
import com.wcs.travel_blog.user.mapper.UserMapper;
import com.wcs.travel_blog.user.model.User;
import com.wcs.travel_blog.user.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    public AuthService(UserRepository userRepository, UserMapper userMapper, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
    }

    public UserDTO registerUser(UserRegistrationDTO userRegistrationDTO, Set<String> roles){
        User user = userMapper.converToEntityOnCreate(userRegistrationDTO, roles);
        if(userRepository.existsByEmail(user.getEmail())){
            throw new EmailAlreadyExistException("Cet email est déjà utilisé");
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        User savedUser = userRepository.save(user);
        return userMapper.converToDto(savedUser);
    }
}
