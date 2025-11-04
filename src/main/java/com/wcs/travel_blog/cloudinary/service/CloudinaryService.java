package com.wcs.travel_blog.cloudinary.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.Transformation;
import com.wcs.travel_blog.cloudinary.config.CloudinaryProperties;
import com.wcs.travel_blog.cloudinary.dto.CloudinarySignatureRequest;
import com.wcs.travel_blog.cloudinary.dto.CloudinarySignatureResponse;
import com.wcs.travel_blog.cloudinary.dto.CloudinaryUrlRequest;
import com.wcs.travel_blog.cloudinary.dto.CloudinaryUrlResponse;
import com.wcs.travel_blog.cloudinary.dto.CloudinaryUploadResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@Service
public class CloudinaryService {

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

        if (StringUtils.hasText(request.getPublicId())) {
            paramsToSign.put("public_id", request.getPublicId());
        }

        if (StringUtils.hasText(request.getFolder())) {
            paramsToSign.put("folder", request.getFolder());
        }

        String uploadPreset = StringUtils.hasText(request.getUploadPreset())
            ? request.getUploadPreset()
            : properties.getUploadPreset();

        if (StringUtils.hasText(uploadPreset)) {
            paramsToSign.put("upload_preset", uploadPreset);
        }

        String signature = cloudinary.apiSignRequest(paramsToSign, properties.getApiSecret());

        return new CloudinarySignatureResponse(
                timestamp,
                signature,
                properties.getApiKey(),
                properties.getCloudName(),
                uploadPreset
        );
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

        return new CloudinaryUrlResponse(publicId, url);
    }

    public CloudinaryUploadResponse uploadFile(MultipartFile file, String folder, String publicId, String resourceType) {
        if (file == null || file.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Le fichier à téléverser est manquant");
        }

        Map<String, Object> options = new HashMap<>();
        if (StringUtils.hasText(folder)) {
            options.put("folder", folder);
        }
        if (StringUtils.hasText(publicId)) {
            options.put("public_id", publicId);
        }

        String resolvedResourceType = StringUtils.hasText(resourceType) ? resourceType : "image";
        options.put("resource_type", resolvedResourceType);

        try {
            byte[] content = file.getBytes();
            Map<?, ?> uploadResult = cloudinary.uploader().upload(content, options);
            String uploadedPublicId = (String) uploadResult.get("public_id");
            String secureUrl = (String) uploadResult.get("secure_url");

            if (!StringUtils.hasText(uploadedPublicId) || !StringUtils.hasText(secureUrl)) {
                throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Réponse Cloudinary incomplète");
            }

            return new CloudinaryUploadResponse(uploadedPublicId, secureUrl, resolvedResourceType);
        } catch (IOException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Erreur lors de l'upload sur Cloudinary", ex);
        } catch (Exception ex) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Cloudinary a refusé le fichier", ex);
        }
    }
}
