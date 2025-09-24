package com.wcs.travel_blog.geocoding.controller;

import com.wcs.travel_blog.geocoding.dto.LocationSuggestionDTO;
import com.wcs.travel_blog.geocoding.service.GeocodingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/geolocations")
public class GeocodingController {

    private final GeocodingService geocodingService;

    public GeocodingController(GeocodingService geocodingService) {
        this.geocodingService = geocodingService;
    }

    @GetMapping("/search")
    public ResponseEntity<List<LocationSuggestionDTO>> searchLocations(@RequestParam("query") String query) {
        List<LocationSuggestionDTO> locations = geocodingService.searchLocations(query);
        return ResponseEntity.ok(locations);
    }
}
