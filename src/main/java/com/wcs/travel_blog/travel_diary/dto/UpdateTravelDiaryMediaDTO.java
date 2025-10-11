package com.wcs.travel_blog.travel_diary.dto;

import com.wcs.travel_blog.media.model.MediaType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateTravelDiaryMediaDTO {

    private Long id;

    private String fileUrl;

    private MediaType mediaType;

    private Boolean isVisible;

    private String publicId;
}
