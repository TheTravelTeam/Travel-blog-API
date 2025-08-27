package com.wcs.travel_blog.media.dto;

import com.wcs.travel_blog.media.model.MediaType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateMediaDTO {
    private String fileUrl;
    private MediaType mediaType;
    private Long stepId;
    private Long TravelDiaryId;
    private Boolean isVisible;
}
