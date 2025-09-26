package com.wcs.travel_blog.cloudinary.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CloudinaryUrlRequest {

    private Integer width;

    private Integer height;

    private String crop;

    private String format;

    private String quality;
}
