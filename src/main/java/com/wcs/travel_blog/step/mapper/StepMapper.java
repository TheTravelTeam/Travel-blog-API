package com.wcs.travel_blog.step.mapper;

import com.wcs.travel_blog.step.dto.StepDTo;

public class StepMapper {

    public StepDTo toDto(StepDTo step) {
        StepDTo stepDto = new StepDTo();
        stepDto.setTitle(step.getTitle());
        stepDto.setDescription(step.getDescription());
        stepDto.setCreatedAt(step.getCreatedAt());
        stepDto.setUpdatedAt(step.getUpdatedAt());
        stepDto.setStartDate(step.getStartDate());
        stepDto.setEndDate(step.getEndDate());
        stepDto.setStatus(step.getStatus());
        stepDto.setLatitude(step.getLatitude());
        stepDto.setLongitude(step.getLongitude());
        stepDto.setCity(step.getCity());
        stepDto.setCountry(step.getCountry());
        stepDto.setContinent(step.getContinent());

        if (step.getTravelDiaryId() != null) {
            stepDto.setTravelDiaryId(step.getTravelDiaryId());
        }
        stepDto.setThemes(step.getThemes());
        return stepDto;
    }
}