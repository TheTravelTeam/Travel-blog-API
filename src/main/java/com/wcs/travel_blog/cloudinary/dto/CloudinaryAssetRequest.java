package com.wcs.travel_blog.cloudinary.dto;

import com.wcs.travel_blog.media.model.MediaType;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CloudinaryAssetRequest {

    @NotBlank
    private String publicId;

    @NotBlank
    private String secureUrl;

    private Boolean isVisible;

    private MediaType mediaType;

    private Long stepId;

    private Long travelDiaryId;

    private Long articleId;
}
