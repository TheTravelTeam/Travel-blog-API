package com.wcs.travel_blog.cloudinary.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CloudinaryUrlResponse {

    private final String publicId;

    private final String url;
}
