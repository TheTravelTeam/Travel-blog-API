package com.wcs.travel_blog.mapper;

import com.wcs.travel_blog.dto.UpsertTravelDiaryDto;
import com.wcs.travel_blog.dto.StepRequestDto;
import com.wcs.travel_blog.dto.TravelDiaryDto;
import com.wcs.travel_blog.model.Media;
import com.wcs.travel_blog.model.Step;
import com.wcs.travel_blog.model.TravelDiary;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

@Component
public class TravelDiaryMapper {
    public TravelDiaryDto mapTravelDiaryToDto(TravelDiary diary) {
        if (diary == null) return null;

        TravelDiaryDto dto = new TravelDiaryDto();
        dto.setId(diary.getId());
        dto.setTitle(diary.getTitle());
        dto.setDescription(diary.getDescription());
        dto.setPrivate(diary.getPrivate());
        dto.setPublished(diary.getPublished());
        dto.setLatitude(diary.getLatitude());
        dto.setLongitude(diary.getLongitude());
        dto.setStatus(diary.getStatus().name());
        dto.setCanComment(diary.getCanComment());
        dto.setCreatedAt(diary.getCreatedAt());
        dto.setUpdatedAt(diary.getUpdatedAt());

        dto.setUser(UserMapper.mapUserToDto(diary.getUser()));

        if (diary.getSteps() != null) {
            dto.setSteps(diary.getSteps().stream()
                    .map(StepMapper::mapSteptoDto)
                    .collect(Collectors.toList()));
        }

        dto.setCoverMedia(MediaMapper.mapToMediaDto(diary.getCoverMedia()));

        return dto;
    }

    public  TravelDiary createTravelDiaryFromCreateTravelDiaryDto(UpsertTravelDiaryDto dto) {
        TravelDiary diary = new TravelDiary();
        diary.setTitle(dto.getTitle());
        diary.setDescription(dto.getDescription());
        diary.setLatitude(dto.getLatitude());
        diary.setLongitude(dto.getLongitude());
        diary.setPrivate(false);
        diary.setPublished(true);
        diary.setStatus(TravelDiary.Status.IN_PROGRESS);
        diary.setCreatedAt(LocalDateTime.now());
        diary.setUpdatedAt(LocalDateTime.now());

        if (dto.getCoverMedia() != null) {
            Media media = new Media();
            media.setFileUrl(dto.getCoverMedia().getFileUrl());
            media.setMediaType(Media.MediaType.valueOf(dto.getCoverMedia().getMediaType()));
            media.setCreatedAt(LocalDateTime.now());
            media.setUpdatedAt(LocalDateTime.now());
            media.setStatus(Media.Status.VISIBLE);
            diary.setCoverMedia(media);
        }

        return diary;
    }

    public  Step createFirstStepFromCreateTravelDiaryDto(UpsertTravelDiaryDto dto, TravelDiary diary) {
        Step firstStep = new Step();
        firstStep.setTitle("Départ");
        firstStep.setDescription("Point de départ");
        firstStep.setLatitude(dto.getLatitude());
        firstStep.setLongitude(dto.getLongitude());
        firstStep.setCreatedAt(LocalDateTime.now());
        firstStep.setUpdatedAt(LocalDateTime.now());
        firstStep.setStatus(Step.Status.COMPLETED);
        firstStep.setTravelDiary(diary);
        return firstStep;
    }

    public  Step createStepFromStepRequestDto(StepRequestDto dto, TravelDiary diary) {
        Step step = new Step();
        step.setTitle(dto.getTitle());
        step.setDescription(dto.getDescription());
        step.setLatitude(dto.getLatitude());
        step.setLongitude(dto.getLongitude());
        step.setTravelDiary(diary);
        return step;
    }

    public  void updateDiaryFromUpsetDiaryDto(UpsertTravelDiaryDto dto, TravelDiary diary) {
        diary.setTitle(dto.getTitle());
        diary.setDescription(dto.getDescription());
        diary.setLatitude(dto.getLatitude());
        diary.setLongitude(dto.getLongitude());
        diary.setUpdatedAt(LocalDateTime.now());

        if (dto.getCoverMedia() != null) {
            Media media = new Media();
            media.setFileUrl(dto.getCoverMedia().getFileUrl());
            media.setMediaType(Media.MediaType.valueOf(dto.getCoverMedia().getMediaType()));
            media.setCreatedAt(LocalDateTime.now());
            media.setUpdatedAt(LocalDateTime.now());
            media.setStatus(Media.Status.VISIBLE);
            diary.setCoverMedia(media);
        }
    }
}

