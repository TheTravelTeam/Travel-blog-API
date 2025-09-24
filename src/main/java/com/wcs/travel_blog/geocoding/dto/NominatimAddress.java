package com.wcs.travel_blog.geocoding.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class NominatimAddress {

    private String city;

    private String town;

    private String village;

    private String municipality;

    private String county;

    private String state;

    private String country;

    @JsonProperty("country_code")
    private String countryCode;

    @JsonProperty("postcode")
    private String postalCode;
}
