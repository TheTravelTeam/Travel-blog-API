package com.wcs.travel_blog.cloudinary.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CloudinarySignatureResponse {

    private final long timestamp;

    private final String signature;

    private final String apiKey;

    private final String cloudName;

    private final String uploadPreset;
}
