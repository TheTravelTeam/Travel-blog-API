package com.wcs.travel_blog.mapper;

import com.wcs.travel_blog.dto.StepThemeDto;
import com.wcs.travel_blog.model.StepTheme;

public class ThemeStepMapper {
    public static StepThemeDto mapThemeStepToDto(StepTheme stepTheme) {
        if (stepTheme == null) return null;

        StepThemeDto dto = new StepThemeDto();
        dto.setId(stepTheme.getId());
        dto.setName(stepTheme.getTheme() != null ? stepTheme.getTheme().getName() : null);
        return dto;
    }
}
