package com.wcs.travel_blog.geocoding;

public class ReverseGeocodingResult {

    private double latitude;
    private double longitude;
    private String city;
    private String country;
    private String continent;

    public ReverseGeocodingResult() {
    }

    public ReverseGeocodingResult(double latitude, double longitude, String city, String country, String continent) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.city = city;
        this.country = country;
        this.continent = continent;
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

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getContinent() {
        return continent;
    }

    public void setContinent(String continent) {
        this.continent = continent;
    }
}
