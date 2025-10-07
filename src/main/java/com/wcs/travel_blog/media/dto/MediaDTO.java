package com.wcs.travel_blog.media.dto;

import com.wcs.travel_blog.media.model.MediaType;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class MediaDTO {
    private Long id;
    private String fileUrl;
    private String publicId;
    private MediaType mediaType;
    private Boolean isVisible;
    private Long stepId;
    private Long articleId;
    private Long travelDiaryId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
