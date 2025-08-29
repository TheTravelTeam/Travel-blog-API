package com.wcs.travel_blog.media.dto;

import com.wcs.travel_blog.media.model.MediaType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateMediaDTO {
    @NotBlank
    private String fileUrl;
    @NotNull
    private MediaType mediaType;
    private Long stepId;
    private Long TravelDiaryId;
    private Boolean isVisible;
}
