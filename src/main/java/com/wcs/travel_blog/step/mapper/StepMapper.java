package com.wcs.travel_blog.step.mapper;

import com.wcs.travel_blog.comment.mapper.CommentMapper;
import com.wcs.travel_blog.media.mapper.MediaMapper;
import com.wcs.travel_blog.step.dto.StepDTO;
import com.wcs.travel_blog.step.model.Step;
import com.wcs.travel_blog.theme.mapper.ThemeMapper;
import com.wcs.travel_blog.travel_diary.model.TravelDiary;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class StepMapper {


    private final ThemeMapper themeMapper;
    private final CommentMapper commentMapper;
    private final MediaMapper mediaMapper;

    public StepMapper(ThemeMapper themeMapper, CommentMapper commentMapper, MediaMapper mediaMapper) {
        this.themeMapper = themeMapper;
        this.commentMapper = commentMapper;
        this.mediaMapper = mediaMapper;
    }

    public StepDTO toDto(Step step) {
        StepDTO stepDTO = new StepDTO();
        stepDTO.setId(step.getId());
        stepDTO.setTitle(step.getTitle());
        stepDTO.setDescription(step.getDescription());
        stepDTO.setCreatedAt(step.getCreatedAt());
        stepDTO.setUpdatedAt(step.getUpdatedAt());
        stepDTO.setStartDate(step.getStartDate());
        stepDTO.setEndDate(step.getEndDate());
        stepDTO.setStatus(step.getStatus());
        stepDTO.setLatitude(step.getLatitude());
        stepDTO.setLongitude(step.getLongitude());
        stepDTO.setCity(step.getCity());
        stepDTO.setCountry(step.getCountry());
        stepDTO.setContinent(step.getContinent());

        if (step.getTravelDiary().getId() != null) {
            stepDTO.setTravelDiaryId(step.getTravelDiary().getId());
        }

        if (step.getMedias() != null && !step.getMedias().isEmpty()) {
            stepDTO.setMedia( // ou setMedias(...) si ton DTO s’appelle "medias"
                    step.getMedias()
                            .stream()
                            .map(mediaMapper::toDto)
                            .collect(Collectors.toList())
            );
        } else {
            stepDTO.setMedia(List.of());
        }

        stepDTO.setComments(step.getComments() != null
                ? step.getComments().stream()
                .map(commentMapper::toDto)
                .collect(Collectors.toList())
                : List.of());

        stepDTO.setThemes(step.getThemes() != null
                ? step.getThemes().stream()
                .map(themeMapper::toDto)
                .collect(Collectors.toList())
                : List.of());
        return stepDTO;
    }

    public Step toEntity(StepDTO stepDto) {
        if (stepDto == null) {
            return null;
        }

        Step step = new Step();
        step.setId(stepDto.getId());
        step.setTitle(stepDto.getTitle());
        step.setDescription(stepDto.getDescription());
        step.setCreatedAt(stepDto.getCreatedAt() != null ? stepDto.getCreatedAt() : step.getCreatedAt());
        step.setUpdatedAt(stepDto.getUpdatedAt() != null ? stepDto.getUpdatedAt() : step.getUpdatedAt());
        step.setStartDate(stepDto.getStartDate());
        step.setEndDate(stepDto.getEndDate());
        step.setStatus(stepDto.getStatus());
        step.setLatitude(stepDto.getLatitude());
        step.setLongitude(stepDto.getLongitude());
        step.setCity(stepDto.getCity());
        step.setCountry(stepDto.getCountry());
        step.setContinent(stepDto.getContinent());

        if (stepDto.getTravelDiaryId() != null) {
            TravelDiary td = new TravelDiary();
            td.setId(stepDto.getTravelDiaryId());
            step.setTravelDiary(td);
        }

        // Themes
        if (stepDto.getThemes() != null) {
            step.setThemes(stepDto.getThemes().stream()
                    .map(themeMapper::toEntity)
                    .collect(Collectors.toList()));
        }

        return step;
    }
}