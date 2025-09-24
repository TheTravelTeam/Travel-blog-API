package com.wcs.travel_blog.geocoding.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class LocationSuggestionDTO {

    private final String displayName;

    private final Double latitude;

    private final Double longitude;

    private final String city;

    private final String state;

    private final String country;

    private final String countryCode;

    private final String postalCode;
}
