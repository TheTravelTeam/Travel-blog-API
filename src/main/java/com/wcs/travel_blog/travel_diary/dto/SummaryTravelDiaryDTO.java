package com.wcs.travel_blog.travel_diary.dto;

import com.wcs.travel_blog.media.dto.MediaDTO;
import com.wcs.travel_blog.step.dto.SummaryStepDTO;
import com.wcs.travel_blog.travel_diary.model.TravelStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class SummaryTravelDiaryDTO {
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

    private List<SummaryStepDTO> steps;

    private MediaDTO media;
}
