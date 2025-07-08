package com.wcs.travel_blog.dto;

import java.util.List;

public record StepRequestDto(
        String title,
        String description,
        Double latitude,
        Double longitude,
        List<PointDto> routePoints
) {}

