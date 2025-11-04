package com.wcs.travel_blog.step.dto;

import com.wcs.travel_blog.comment.dto.CommentDTO;
import com.wcs.travel_blog.media.dto.MediaDTO;
import com.wcs.travel_blog.theme.dto.ThemeDTO;
import com.wcs.travel_blog.travel_diary.model.TravelStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class StepResponseDTO {

    private Long id;

    private String title;

    private String description;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private LocalDate startDate;

    private LocalDate endDate;

    private TravelStatus status;

    private Long likesCount = 0L;

    private Long likes = 0L;

    private Boolean viewerHasLiked = Boolean.FALSE;

    private Double latitude;

    private Double longitude;

    private String city;

    private String country;

    private String continent;

    private Long travelDiaryId;

    private List<Long> themeIds = List.of();

    private List<ThemeDTO> themes = List.of();

    private List<CommentDTO> comments = List.of();

    private List<MediaDTO> media = List.of();
}
