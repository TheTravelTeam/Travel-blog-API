package com.wcs.travel_blog.media.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class MediaDTO {
    private Long id;
    private String fileUrl;
    private String mediaType;
    private Boolean isVisible;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
