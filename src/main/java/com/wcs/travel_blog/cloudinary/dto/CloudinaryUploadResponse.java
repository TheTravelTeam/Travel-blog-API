package com.wcs.travel_blog.cloudinary.dto;

public class CloudinaryUploadResponse {

    private String publicId;
    private String secureUrl;
    private String resourceType;

    public CloudinaryUploadResponse() {
    }

    public CloudinaryUploadResponse(String publicId, String secureUrl, String resourceType) {
        this.publicId = publicId;
        this.secureUrl = secureUrl;
        this.resourceType = resourceType;
    }

    public String getPublicId() {
        return publicId;
    }

    public void setPublicId(String publicId) {
        this.publicId = publicId;
    }

    public String getSecureUrl() {
        return secureUrl;
    }

    public void setSecureUrl(String secureUrl) {
        this.secureUrl = secureUrl;
    }

    public String getResourceType() {
        return resourceType;
    }

    public void setResourceType(String resourceType) {
        this.resourceType = resourceType;
    }
}
