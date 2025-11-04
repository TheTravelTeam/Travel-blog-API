package com.wcs.travel_blog.cloudinary.controller;

import com.wcs.travel_blog.cloudinary.dto.CloudinaryAssetRequest;
import com.wcs.travel_blog.cloudinary.dto.CloudinarySignatureRequest;
import com.wcs.travel_blog.cloudinary.dto.CloudinarySignatureResponse;
import com.wcs.travel_blog.cloudinary.dto.CloudinaryUploadResponse;
import com.wcs.travel_blog.cloudinary.dto.CloudinaryUrlRequest;
import com.wcs.travel_blog.cloudinary.dto.CloudinaryUrlResponse;
import com.wcs.travel_blog.cloudinary.service.CloudinaryService;
import com.wcs.travel_blog.media.dto.MediaDTO;
import com.wcs.travel_blog.media.service.MediaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/cloudinary")
public class CloudinaryController {

    private final CloudinaryService cloudinaryService;
    private final MediaService mediaService;

    public CloudinaryController(CloudinaryService cloudinaryService,
                                MediaService mediaService) {
        this.cloudinaryService = cloudinaryService;
        this.mediaService = mediaService;
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/signature")
    public ResponseEntity<CloudinarySignatureResponse> generateSignature(@Valid @RequestBody CloudinarySignatureRequest request,
                                                                          @AuthenticationPrincipal(expression = "id") Long currentUserId) {
        CloudinarySignatureResponse response = cloudinaryService.generateSignature(request);
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<CloudinaryUploadResponse> uploadMedia(
            @RequestPart("file") MultipartFile file,
            @RequestParam(value = "folder", required = false) String folder,
            @RequestParam(value = "publicId", required = false) String publicId,
            @RequestParam(value = "resourceType", required = false) String resourceType,
            @AuthenticationPrincipal(expression = "id") Long currentUserId
    ) {
        CloudinaryUploadResponse response = cloudinaryService.uploadFile(file, folder, publicId, resourceType);
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/media")
    public ResponseEntity<MediaDTO> registerAsset(@Valid @RequestBody CloudinaryAssetRequest request,
                                                  @AuthenticationPrincipal(expression = "id") Long currentUserId) {
        MediaDTO media = mediaService.saveFromCloudinary(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(media);
    }

    @GetMapping("/media/{publicId}")
    public ResponseEntity<CloudinaryUrlResponse> resolvePublicUrl(@PathVariable String publicId,
                                                                  @ModelAttribute CloudinaryUrlRequest urlRequest) {
        mediaService.getMediaByPublicId(publicId);
        CloudinaryUrlResponse response = cloudinaryService.buildDeliveryUrl(publicId, urlRequest);
        return ResponseEntity.ok(response);
    }
}
