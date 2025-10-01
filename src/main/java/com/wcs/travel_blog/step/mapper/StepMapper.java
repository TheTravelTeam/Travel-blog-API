package com.wcs.travel_blog.step.mapper;

import com.wcs.travel_blog.comment.mapper.CommentMapper;
import com.wcs.travel_blog.media.mapper.MediaMapper;
import com.wcs.travel_blog.step.dto.StepRequestDTO;
import com.wcs.travel_blog.step.dto.StepResponseDTO;
import com.wcs.travel_blog.step.model.Step;
import com.wcs.travel_blog.theme.mapper.ThemeMapper;
import com.wcs.travel_blog.theme.model.Theme;
import com.wcs.travel_blog.travel_diary.model.TravelDiary;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
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

    public StepResponseDTO toResponseDto(Step step) {
        StepResponseDTO dto = new StepResponseDTO();
        dto.setId(step.getId());
        dto.setTitle(step.getTitle());
        dto.setDescription(step.getDescription());
        dto.setCreatedAt(step.getCreatedAt());
        dto.setUpdatedAt(step.getUpdatedAt());
        dto.setStartDate(step.getStartDate());
        dto.setEndDate(step.getEndDate());
        dto.setStatus(step.getStatus());
        dto.setLikesCount(step.getLikesCount() != null ? step.getLikesCount() : 0L);
        dto.setLatitude(step.getLatitude());
        dto.setLongitude(step.getLongitude());
        dto.setCity(step.getCity());
        dto.setCountry(step.getCountry());
        dto.setContinent(step.getContinent());

        if (step.getTravelDiary() != null && step.getTravelDiary().getId() != null) {
            dto.setTravelDiaryId(step.getTravelDiary().getId());
        }

        if (step.getMedias() != null && !step.getMedias().isEmpty()) {
            dto.setMedia(step.getMedias().stream()
                    .map(mediaMapper::toDto)
                    .collect(Collectors.toList()));
        } else {
            dto.setMedia(List.of());
        }

        dto.setComments(step.getComments() != null
                ? step.getComments().stream()
                .map(commentMapper::toDto)
                .collect(Collectors.toList())
                : List.of());

        if (step.getThemes() != null && !step.getThemes().isEmpty()) {
            dto.setThemes(step.getThemes().stream()
                    .map(themeMapper::toDto)
                    .collect(Collectors.toList()));
            dto.setThemeIds(step.getThemes().stream()
                    .map(Theme::getId)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList()));
        } else {
            dto.setThemes(List.of());
            dto.setThemeIds(List.of());
        }

        return dto;
    }

    public Step toEntity(StepRequestDTO stepDto) {
        if (stepDto == null) {
            return null;
        }

        Step step = new Step();
        step.setTitle(stepDto.getTitle());
        step.setDescription(stepDto.getDescription());
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

        return step;
    }
}
