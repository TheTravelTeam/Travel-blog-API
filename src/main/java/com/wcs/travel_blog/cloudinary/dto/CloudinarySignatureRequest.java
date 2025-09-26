package com.wcs.travel_blog.cloudinary.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CloudinarySignatureRequest {

    private String folder;

    private String publicId;

    private String resourceType;

    private String uploadPreset;
}
