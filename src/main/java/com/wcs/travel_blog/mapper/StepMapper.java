package com.wcs.travel_blog.mapper;

import com.wcs.travel_blog.dto.StepDto;
import com.wcs.travel_blog.model.Step;

import java.util.stream.Collectors;

public class StepMapper {
    public static StepDto mapSteptoDto(Step step) {
        if (step == null) return null;

        StepDto dto = new StepDto();
        dto.setId(step.getId());
        dto.setTitle(step.getTitle());
        dto.setDescription(step.getDescription());
        dto.setCreatedAt(step.getCreatedAt());
        dto.setUpdatedAt(step.getUpdatedAt());
        dto.setStartDate(step.getStartDate());
        dto.setEndDate(step.getEndDate());
        dto.setStatus(step.getStatus().name());
        dto.setLocation(step.getLocation());
        dto.setCountry(step.getCountry());
        dto.setContinent(step.getContinent());
        dto.setLatitude(step.getLatitude());
        dto.setLongitude(step.getLongitude());

        if (step.getMedias() != null) {
            dto.setMedias(step.getMedias().stream()
                    .map(MediaMapper::mapToMediaDto)
                    .collect(Collectors.toList()));
        }

        if (step.getComments() != null) {
            dto.setComments(step.getComments().stream()
                    .map(CommentMapper::mapCommentToDto)
                    .collect(Collectors.toList()));
        }

        if (step.getStepThemes() != null) {
            dto.setStepThemes(step.getStepThemes().stream()
                    .map(ThemeStepMapper::mapThemeStepToDto)
                    .collect(Collectors.toList()));
        }

        return dto;
    }
}
