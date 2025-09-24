package com.wcs.travel_blog.geocoding.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class NominatimPlaceResponse {

    private String latitude;

    private String longitude;

    @JsonProperty("display_name")
    private String displayName;

    @JsonProperty("lat")
    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    @JsonProperty("lon")
    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    private NominatimAddress address;
}
