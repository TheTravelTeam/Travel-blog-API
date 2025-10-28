package com.wcs.travel_blog.cloudinary.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CloudinarySignatureRequest {
    private String publicId;

    private String uploadPreset;

    private String folder;
}
