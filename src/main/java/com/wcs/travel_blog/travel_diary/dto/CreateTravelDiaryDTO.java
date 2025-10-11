package com.wcs.travel_blog.travel_diary.dto;

import com.wcs.travel_blog.media.dto.CreateMediaDTO;
import com.wcs.travel_blog.travel_diary.model.TravelStatus;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;


@Getter
@Setter

@Component
public class CreateTravelDiaryDTO {

    private String title;

    private String description;

    private Boolean isPrivate;

    private Boolean isPublished;

    private TravelStatus status;

    private LocalDate startDate;

    private LocalDate endDate;

    private Boolean canComment;

    private Double latitude;

    private Double longitude;

    private List<Long> steps;

    private Long user;

    private CreateMediaDTO media;
}
