package com.wcs.travel_blog.travel_diary.mapper;

import com.wcs.travel_blog.step.model.Step;
import com.wcs.travel_blog.travel_diary.dto.CreateTravelDiaryDTO;
import com.wcs.travel_blog.travel_diary.dto.TravelDiaryDTO;
import com.wcs.travel_blog.travel_diary.dto.UpdateTravelDiaryDTO;
import com.wcs.travel_blog.travel_diary.model.TravelDiary;
import com.wcs.travel_blog.user.model.User;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class TravelDiaryMapper {

    public TravelDiaryDTO toDto(TravelDiary travelDiary){
        TravelDiaryDTO dto = new TravelDiaryDTO();
        dto.setTitle(travelDiary.getTitle());
        dto.setDescription(travelDiary.getDescription());
        dto.setCreatedAt(travelDiary.getCreatedAt());
        dto.setUpdatedAt(travelDiary.getUpdatedAt());
        dto.setIsPrivate(travelDiary.getIsPrivate());
        dto.setIsPublished(travelDiary.getIsPublished());
        dto.setStatus(travelDiary.getStatus());
        dto.setCanComment(travelDiary.getCanComment());
        dto.setLatitude(travelDiary.getLatitude());
        dto.setLongitude(travelDiary.getLongitude());

        dto.setUser(travelDiary.getUser() != null ? travelDiary.getUser().getId() : null);
        dto.setMedia(travelDiary.getMedia() != null ? travelDiary.getMedia().getId() : null);
        if (travelDiary.getSteps() != null) {
            dto.setSteps(travelDiary.getSteps().stream()
                    .map(Step::getId)
                    .collect(Collectors.toList()));
        } else {
            dto.setSteps(List.of());
        }

        return dto;

    }

    public TravelDiary toEntity(CreateTravelDiaryDTO dto) {
        TravelDiary travelDiary = new TravelDiary();

        travelDiary.setTitle(dto.getTitle());
        travelDiary.setDescription(dto.getDescription());
        travelDiary.setIsPrivate(dto.getIsPrivate());
        travelDiary.setIsPublished(dto.getIsPublished());
        travelDiary.setStatus(dto.getStatus());
        travelDiary.setCanComment(dto.getCanComment());
        travelDiary.setLatitude(dto.getLatitude());
        travelDiary.setLongitude(dto.getLongitude());

        travelDiary.setCreatedAt(LocalDateTime.now());
        travelDiary.setUpdatedAt(LocalDateTime.now());

        return travelDiary;
    }

}
