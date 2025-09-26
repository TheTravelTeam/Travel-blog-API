package com.wcs.travel_blog.cloudinary.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.Transformation;
import com.wcs.travel_blog.cloudinary.config.CloudinaryProperties;
import com.wcs.travel_blog.cloudinary.dto.CloudinarySignatureRequest;
import com.wcs.travel_blog.cloudinary.dto.CloudinarySignatureResponse;
import com.wcs.travel_blog.cloudinary.dto.CloudinaryUrlRequest;
import com.wcs.travel_blog.cloudinary.dto.CloudinaryUrlResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@Service
@ConditionalOnProperty(prefix = "cloudinary", name = "enabled", havingValue = "true")
public class CloudinaryService {

    private static final Logger LOGGER = LoggerFactory.getLogger(CloudinaryService.class);

    private final Cloudinary cloudinary;

    private final CloudinaryProperties properties;

    public CloudinaryService(Cloudinary cloudinary, CloudinaryProperties properties) {
        this.cloudinary = cloudinary;
        this.properties = properties;
    }

    public CloudinarySignatureResponse generateSignature(CloudinarySignatureRequest request) {
        long timestamp = Instant.now().getEpochSecond();
        Map<String, Object> paramsToSign = new HashMap<>();
        paramsToSign.put("timestamp", timestamp);

        if (StringUtils.hasText(request.getFolder())) {
            paramsToSign.put("folder", request.getFolder());
        }

        if (StringUtils.hasText(request.getPublicId())) {
            paramsToSign.put("public_id", request.getPublicId());
        }

        String uploadPreset = StringUtils.hasText(request.getUploadPreset())
            ? request.getUploadPreset()
            : properties.getUploadPreset();

        if (StringUtils.hasText(uploadPreset)) {
            paramsToSign.put("upload_preset", uploadPreset);
        }

        String signature = cloudinary.apiSignRequest(paramsToSign, properties.getApiSecret());
        LOGGER.debug("Signature Cloudinary générée pour le dossier {} et publicId {}", request.getFolder(), request.getPublicId());

        return CloudinarySignatureResponse.builder()
            .timestamp(timestamp)
            .signature(signature)
            .apiKey(properties.getApiKey())
            .cloudName(properties.getCloudName())
            .uploadPreset(uploadPreset)
            .build();
    }

    public CloudinaryUrlResponse buildDeliveryUrl(String publicId, CloudinaryUrlRequest request) {
        Transformation transformation = new Transformation();

        if (request != null) {
            if (request.getWidth() != null) {
                transformation.width(request.getWidth());
            }
            if (request.getHeight() != null) {
                transformation.height(request.getHeight());
            }
            if (StringUtils.hasText(request.getCrop())) {
                transformation.crop(request.getCrop());
            }
            if (StringUtils.hasText(request.getQuality())) {
                transformation.quality(request.getQuality());
            }
            if (StringUtils.hasText(request.getFormat())) {
                transformation.fetchFormat(request.getFormat());
            }
        }

        String url = cloudinary.url()
            .secure(true)
            .transformation(transformation)
            .generate(publicId);

        LOGGER.debug("URL Cloudinary générée pour {}", publicId);
        return CloudinaryUrlResponse.builder()
            .publicId(publicId)
            .url(url)
            .build();
    }
}
