package com.wcs.travel_blog.travel_diary.dto;

import com.wcs.travel_blog.media.dto.MediaDTO;
import com.wcs.travel_blog.step.dto.StepResponseDTO;
import com.wcs.travel_blog.travel_diary.model.TravelStatus;
import com.wcs.travel_blog.user.dto.UserDTO;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter

@Component
public class TravelDiaryDTO {

    private Long id;

    private String title;

    private String description;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private Boolean isPrivate;

    private Boolean isPublished;

    private TravelStatus status;

    private LocalDate startDate;

    private LocalDate endDate;

    private Boolean canComment;

    private Double latitude;

    private Double longitude;

    private List<StepResponseDTO> steps;

    private UserDTO user;

    private MediaDTO media;
}
