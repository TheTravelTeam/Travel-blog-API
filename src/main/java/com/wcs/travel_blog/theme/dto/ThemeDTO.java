package com.wcs.travel_blog.theme.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ThemeDTO {
    private Long id;

    @NotBlank(message = "Le nom du thème ne doit pas être vide")
    @Size(min = 2, max = 100, message = "Le nom du thème doit contenir entre 2 et 100 caractères")
    private String name;
    
    private LocalDateTime updatedAt;
}
