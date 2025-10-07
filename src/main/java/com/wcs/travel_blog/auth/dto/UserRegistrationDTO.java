package com.wcs.travel_blog.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserRegistrationDTO {
    @NotBlank(message = "Le nom d'utilisateur est obligatoire")
    @Size(min= 3, max = 50)
    private String pseudo;
    @Size(min = 8, message = "Le mot de passe doit faire au moins 8 caractères")
    private String password;
    @Email(message = "L'email est invalide")
    @NotBlank(message = "L'email est obligatoire")
    private String email;
}
