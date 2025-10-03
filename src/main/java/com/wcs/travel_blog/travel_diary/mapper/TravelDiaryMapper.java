package com.wcs.travel_blog.travel_diary.mapper;

import com.wcs.travel_blog.media.mapper.MediaMapper;
import com.wcs.travel_blog.step.mapper.StepMapper;
import com.wcs.travel_blog.travel_diary.dto.CreateTravelDiaryDTO;
import com.wcs.travel_blog.travel_diary.dto.TravelDiaryDTO;
import com.wcs.travel_blog.travel_diary.model.TravelDiary;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class TravelDiaryMapper {

    private final StepMapper stepMapper;
    private final MediaMapper mediaMapper;

    public TravelDiaryMapper(StepMapper stepMapper, MediaMapper mediaMapper) {
        this.stepMapper = stepMapper;
        this.mediaMapper = mediaMapper;
    }

    public TravelDiaryDTO toDto(TravelDiary travelDiary){
        TravelDiaryDTO dto = new TravelDiaryDTO();
        dto.setTitle(travelDiary.getTitle());
        dto.setDescription(travelDiary.getDescription());
        dto.setCreatedAt(travelDiary.getCreatedAt());
        dto.setUpdatedAt(travelDiary.getUpdatedAt());
        dto.setIsPrivate(travelDiary.getIsPrivate());
        dto.setIsPublished(travelDiary.getIsPublished());
        dto.setStatus(travelDiary.getStatus());
        dto.setStartDate(travelDiary.getStartDate());
        dto.setEndDate(travelDiary.getEndDate());
        dto.setCanComment(travelDiary.getCanComment());
        dto.setLatitude(travelDiary.getLatitude());
        dto.setLongitude(travelDiary.getLongitude());
        dto.setId(travelDiary.getId());

        dto.setUser(travelDiary.getUser() != null ? travelDiary.getUser().getId() : null);
        if( travelDiary.getMedia() != null){
            dto.setMedia(mediaMapper.toDto(travelDiary.getMedia()));
        } else {
            dto.setMedia(null);
        }

        if (travelDiary.getSteps() != null) {
            dto.setSteps(travelDiary.getSteps().stream()
                    .map(stepMapper::toResponseDto)
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
        travelDiary.setStartDate(dto.getStartDate());
        travelDiary.setEndDate(dto.getEndDate());
        travelDiary.setCanComment(dto.getCanComment());
        travelDiary.setLatitude(dto.getLatitude());
        travelDiary.setLongitude(dto.getLongitude());

        travelDiary.setCreatedAt(LocalDateTime.now());
        travelDiary.setUpdatedAt(LocalDateTime.now());

        return travelDiary;
    }

}
