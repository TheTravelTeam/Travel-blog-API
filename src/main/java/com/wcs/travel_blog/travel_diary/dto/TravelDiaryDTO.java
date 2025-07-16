package com.wcs.travel_blog.travel_diary.dto;

import com.wcs.travel_blog.travel_diary.model.TravelStatus;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter

@Component
public class TravelDiaryDTO {
    private String title;

    private String description;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private Boolean isPrivate;

    private Boolean isPublished;

    private TravelStatus status;

    private Boolean canComment;

    private Double latitude;

    private Double longitude;

    private List<Long> steps;

    private Long user;

    private Long media;
}
