package com.wcs.travel_blog.auth.service;

import com.wcs.travel_blog.auth.dto.UserRegistrationDTO;
import com.wcs.travel_blog.exception.AuthenticationFailedException;
import com.wcs.travel_blog.exception.EmailAlreadyExistException;
import com.wcs.travel_blog.exception.PseudoAlreadyExistException;
import com.wcs.travel_blog.security.JWTService;
import com.wcs.travel_blog.util.HtmlSanitizerService;
import org.springframework.security.authentication.AuthenticationManager;
import com.wcs.travel_blog.user.dto.UserDTO;
import com.wcs.travel_blog.user.mapper.UserMapper;
import com.wcs.travel_blog.user.model.User;
import com.wcs.travel_blog.user.repository.UserRepository;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JWTService jwtService;
    private final HtmlSanitizerService htmlSanitizerService;


    public AuthService(UserRepository userRepository, UserMapper userMapper, PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager, JWTService jwtService, HtmlSanitizerService htmlSanitizerService) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.htmlSanitizerService = htmlSanitizerService;
    }

    public UserDTO registerUser(UserRegistrationDTO userRegistrationDTO, Set<String> roles){
        User user = userMapper.converToEntityOnCreate(userRegistrationDTO, roles);
        if(userRepository.existsByEmail(user.getEmail())){
            throw new EmailAlreadyExistException("Cet email est déjà utilisé");
        }
        if (userRepository.existsByPseudo(user.getPseudo())) {
            throw new PseudoAlreadyExistException("Ce pseudo est déjà utilisé");
        }

        user.setPseudo(htmlSanitizerService.sanitize(user.getPseudo()));

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        User savedUser = userRepository.save(user);
        return userMapper.converToDto(savedUser);
    }

    public String authenticate(String email, String password) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(email, password)
            );
            return jwtService.generateToken((UserDetails) authentication.getPrincipal());
        } catch (BadCredentialsException e) {
            throw new AuthenticationFailedException("Identifiants invalides, vérifiez votre email ou votre mot de passe.");
        }
    }
}
