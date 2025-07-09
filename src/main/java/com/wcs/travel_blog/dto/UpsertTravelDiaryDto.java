package com.wcs.travel_blog.dto;

public class UpsertTravelDiaryDto {
    private String title;
    private String description;
    private double latitude;
    private double longitude;
    private MediaDto coverMedia; // 👈 Ajout

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public MediaDto getCoverMedia() {
        return coverMedia;
    }

    public void setCoverMedia(MediaDto coverMedia) {
        this.coverMedia = coverMedia;
    }
}
