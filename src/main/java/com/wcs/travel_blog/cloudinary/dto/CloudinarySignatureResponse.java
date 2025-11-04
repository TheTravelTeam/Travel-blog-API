package com.wcs.travel_blog.cloudinary.dto;

public class CloudinarySignatureResponse {

    private long timestamp;
    private String signature;
    private String apiKey;
    private String cloudName;
    private String uploadPreset;

    public CloudinarySignatureResponse() {
    }

    public CloudinarySignatureResponse(long timestamp, String signature, String apiKey, String cloudName, String uploadPreset) {
        this.timestamp = timestamp;
        this.signature = signature;
        this.apiKey = apiKey;
        this.cloudName = cloudName;
        this.uploadPreset = uploadPreset;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public String getCloudName() {
        return cloudName;
    }

    public void setCloudName(String cloudName) {
        this.cloudName = cloudName;
    }

    public String getUploadPreset() {
        return uploadPreset;
    }

    public void setUploadPreset(String uploadPreset) {
        this.uploadPreset = uploadPreset;
    }
}
