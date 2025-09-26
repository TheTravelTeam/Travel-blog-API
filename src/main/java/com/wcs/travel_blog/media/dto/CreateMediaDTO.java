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
    private String publicId;
    private String folder;
    private String resourceType;
    private String format;
    private Long bytes;
    private Integer width;
    private Integer height;
    @NotNull
    private MediaType mediaType;
    private Long stepId;
    private Long travelDiaryId;
    private Boolean isVisible;
}
