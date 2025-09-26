package com.wcs.travel_blog.cloudinary.controller;

import com.wcs.travel_blog.cloudinary.dto.CloudinaryAssetRequest;
import com.wcs.travel_blog.cloudinary.dto.CloudinarySignatureRequest;
import com.wcs.travel_blog.cloudinary.dto.CloudinarySignatureResponse;
import com.wcs.travel_blog.cloudinary.dto.CloudinaryUrlRequest;
import com.wcs.travel_blog.cloudinary.dto.CloudinaryUrlResponse;
import com.wcs.travel_blog.cloudinary.service.CloudinaryService;
import com.wcs.travel_blog.exception.FeatureDisabledException;
import com.wcs.travel_blog.exception.TooManyRequestsException;
import com.wcs.travel_blog.media.dto.MediaDTO;
import com.wcs.travel_blog.media.service.MediaService;
import com.wcs.travel_blog.security.RateLimiterService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;

@RestController
@RequestMapping("/cloudinary")
public class CloudinaryController {

    private static final Logger LOGGER = LoggerFactory.getLogger(CloudinaryController.class);

    private static final String SIGNATURE_KEY_PREFIX = "cld:signature:";

    private static final String ASSET_KEY_PREFIX = "cld:asset:";

    private final ObjectProvider<CloudinaryService> cloudinaryServiceProvider;

    private final MediaService mediaService;

    private final RateLimiterService rateLimiterService;

    public CloudinaryController(ObjectProvider<CloudinaryService> cloudinaryServiceProvider,
                                MediaService mediaService,
                                RateLimiterService rateLimiterService) {
        this.cloudinaryServiceProvider = cloudinaryServiceProvider;
        this.mediaService = mediaService;
        this.rateLimiterService = rateLimiterService;
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/signature")
    public ResponseEntity<CloudinarySignatureResponse> generateSignature(@Valid @RequestBody CloudinarySignatureRequest request,
                                                                          @AuthenticationPrincipal(expression = "id") Long currentUserId) {
        enforceRateLimit(SIGNATURE_KEY_PREFIX + currentUserId, 10, Duration.ofMinutes(1));
        LOGGER.info("Signature Cloudinary demandée par l'utilisateur {} pour le dossier {}", currentUserId, request.getFolder());
        CloudinarySignatureResponse response = requireCloudinaryService().generateSignature(request);
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/media")
    public ResponseEntity<MediaDTO> registerAsset(@Valid @RequestBody CloudinaryAssetRequest request,
                                                  @AuthenticationPrincipal(expression = "id") Long currentUserId) {
        requireCloudinaryService();
        enforceRateLimit(ASSET_KEY_PREFIX + currentUserId, 15, Duration.ofMinutes(5));
        LOGGER.info("Enregistrement d'un média Cloudinary {} par l'utilisateur {}", request.getPublicId(), currentUserId);
        MediaDTO media = mediaService.saveFromCloudinary(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(media);
    }

    @GetMapping("/media/{publicId}")
    public ResponseEntity<CloudinaryUrlResponse> resolvePublicUrl(@PathVariable String publicId,
                                                                  @ModelAttribute CloudinaryUrlRequest urlRequest) {
        mediaService.getMediaByPublicId(publicId);
        CloudinaryUrlResponse response = requireCloudinaryService().buildDeliveryUrl(publicId, urlRequest);
        return ResponseEntity.ok(response);
    }

    private void enforceRateLimit(String key, int limit, Duration window) {
        boolean allowed = rateLimiterService.tryConsume(key, limit, window);
        if (!allowed) {
            throw new TooManyRequestsException("Limite de requêtes Cloudinary atteinte, réessayez plus tard.");
        }
    }

    private CloudinaryService requireCloudinaryService() {
        CloudinaryService service = cloudinaryServiceProvider.getIfAvailable();
        if (service == null) {
            throw new FeatureDisabledException("Le service Cloudinary est désactivé.");
        }
        return service;
    }
}
