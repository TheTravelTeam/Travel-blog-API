package com.wcs.travel_blog.auth.controller;

import com.wcs.travel_blog.auth.dto.ForgotPasswordRequestDTO;
import com.wcs.travel_blog.auth.dto.PasswordResetRequestDTO;
import com.wcs.travel_blog.auth.dto.UserLoginDTO;
import com.wcs.travel_blog.auth.dto.UserRegistrationDTO;
import com.wcs.travel_blog.auth.service.AuthService;
import com.wcs.travel_blog.auth.service.PasswordResetService;
import com.wcs.travel_blog.exception.ExternalServiceException;
import com.wcs.travel_blog.exception.ResourceNotFoundException;
import com.wcs.travel_blog.user.dto.UserDTO;
import com.wcs.travel_blog.user.service.UserService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.util.Set;

@Slf4j
@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;
    private final UserService userService;
    private final PasswordResetService passwordResetService;

    @Value("${jwt.cookie.secure}")
    private boolean cookieSecure;

    @Value("${jwt.cookie.samesite}")
    private String sameSite;

    public AuthController(AuthService authService, UserService userService, PasswordResetService passwordResetService){
        this.authService = authService;
        this.userService = userService;
        this.passwordResetService = passwordResetService;
    }

    @GetMapping("/me")
    public ResponseEntity<UserDTO> getCurrentUser(Authentication authentication){
        if (authentication == null || !authentication.isAuthenticated()){
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        String email = authentication.getName();
        UserDTO user = userService.getUserByEmail(email);
        return ResponseEntity.ok(user);
    }

    @PostMapping("/register")
    public ResponseEntity<UserDTO> registerUser(@Valid @RequestBody UserRegistrationDTO userRegistrationDTO){
        UserDTO registeredUser = authService.registerUser(
                userRegistrationDTO,
                Set.of("ROLE_USER")); // Rôle par défaut d'un utilisateur
    return ResponseEntity.status(HttpStatus.CREATED).body(registeredUser);
    }

    @PostMapping("/login")
    public ResponseEntity<String> authenticate(@RequestBody UserLoginDTO userLoginDTO) {
        String token = authService.authenticate(
                userLoginDTO.getEmail(),
                userLoginDTO.getPassword()
        );

        ResponseCookie cookie = ResponseCookie.from("jwt", token)
                .httpOnly(true)
                .secure(cookieSecure)
                .path("/")
                .maxAge(Duration.ofHours(1))
                .sameSite(sameSite)
                .build();
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body("Connexion réussie");
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(){
        ResponseCookie cookie = ResponseCookie.from("jwt", "")
                .httpOnly(true)
                .secure(cookieSecure)
                .path("/")
                .maxAge(0)
                .build();
        return ResponseEntity.noContent()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .build();
    }


    /**
     * Launches the forgot password flow while always returning 204 to keep account existence private.
     */
    @PostMapping("/forgot-password")
    public ResponseEntity<Void> forgotPassword(@Valid @RequestBody ForgotPasswordRequestDTO request) {
        try {
            passwordResetService.requestPasswordReset(request.getEmail());
        } catch (ExternalServiceException e) {
            // On ne révèle pas les problèmes SMTP à l'utilisateur
            log.warn("Échec d'envoi de l'email de réinitialisation : {}", e.getMessage());
        } catch (ResourceNotFoundException e) {
            // On masque aussi le fait qu'un utilisateur n'existe pas
            log.info("Demande de réinitialisation pour un email inconnu : {}", request.getEmail());
        }
        return ResponseEntity.noContent().build();
    }

    /**
     * Confirms password reset with a valid token and updates the user's credentials.
     */
    @PostMapping("/reset-password")
    public ResponseEntity<Void> resetPassword(@Valid @RequestBody PasswordResetRequestDTO request) {
        passwordResetService.resetPassword(request.getToken(), request.getPassword());
        return ResponseEntity.noContent().build();
    }

}
