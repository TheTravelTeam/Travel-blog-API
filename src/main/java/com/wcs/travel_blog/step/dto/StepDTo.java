package com.wcs.travel_blog.step.dto;

import com.wcs.travel_blog.theme.dto.ThemeDTO;
import com.wcs.travel_blog.travel_diary.model.TravelStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class StepDTo {

    @NotBlank(message = "Le titre ne doit pas être vide")
    @Size(min = 2, max = 50, message = "Le titre doit contenir entre 2 et 50 caractères")
    private String title;

    @Size(max = 5000, message = "La description ne doit pas dépasser 5000 caractères")
    private String description;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private LocalDate startDate;
    private LocalDate endDate;

    private TravelStatus status;

    private Double latitude;
    private Double longitude;

    @Size(min = 2, max = 100, message = "La ville doit contenir entre 2 et 100 caractères")
    private String city;

    @Size(min = 2, max = 100, message = "Le pays doit contenir entre 2 et 100 caractères")
    private String country;

    @Size(min = 2, max = 50, message = "Le continent doit contenir entre 2 et 100 caractères")
    private String continent;

    @NotNull(message = "L'ID du carnet de voyage est requis")
    private Long travelDiaryId;

    private List<ThemeDTO> themes;
}
