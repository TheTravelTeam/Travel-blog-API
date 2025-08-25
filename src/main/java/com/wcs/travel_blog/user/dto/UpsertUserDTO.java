package com.wcs.travel_blog.user.dto;


import com.wcs.travel_blog.user.model.UserStatus;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.URL;

@Getter
@Setter
public class UpsertUserDTO {
    private Long id;

    @NotBlank(message = "Le nom d'utilisateur est obligatoire")
    @Size(max = 50)
    private String username;

    @Size(min = 8, message = "Le mot de passe doit faire au moins 8 caractères")
    private String password;

    @Email(message = "L'email est invalide")
    @NotBlank(message = "L'email est obligatoire")
    private String email;

    private String biography;

    @URL(message = "L'URL de l'image doit être valide")
    private String avatar;

    private UserStatus status;
}
