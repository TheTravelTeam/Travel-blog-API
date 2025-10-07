package com.wcs.travel_blog.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserLoginDTO {
    @Email(message = "L'email est invalide")
    @NotBlank(message = "L'email est obligatoire")
    private String email;
    private String password;

}
