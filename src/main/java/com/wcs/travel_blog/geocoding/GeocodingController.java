package com.wcs.travel_blog.geocoding;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Validated
@RequestMapping("/geocoding")
public class GeocodingController {

    private final ReverseGeocodingService reverseGeocodingService;

    public GeocodingController(ReverseGeocodingService reverseGeocodingService) {
        this.reverseGeocodingService = reverseGeocodingService;
    }

    @GetMapping("/reverse")
    public ResponseEntity<ReverseGeocodingResult> reverseGeocode(
            @RequestParam("lat") double latitude,
            @RequestParam("lon") double longitude
    ) {
        ReverseGeocodingResult result = reverseGeocodingService.reverseGeocode(latitude, longitude);
        return ResponseEntity.ok(result);
    }
}
