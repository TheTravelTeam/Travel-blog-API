package com.wcs.travel_blog.step.mapper;

import com.wcs.travel_blog.comment.mapper.CommentMapper;
import com.wcs.travel_blog.media.mapper.MediaMapper;
import com.wcs.travel_blog.step.dto.StepDTO;
import com.wcs.travel_blog.step.model.Step;
import com.wcs.travel_blog.theme.mapper.ThemeMapper;
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

        if (step.getComments() != null && !step.getComments().isEmpty()) {
            stepDTO.setComments(
                    step.getComments()
                            .stream()
                            .map(commentMapper::toDto)
                            .collect(Collectors.toList())
            );
        } else {
            stepDTO.setComments(List.of());
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

        if (step.getTravelDiary() != null && step.getTravelDiary().getId() != null) {
            stepDTO.setTravelDiaryId(step.getTravelDiary().getId());
        } else {
            stepDTO.setTravelDiaryId(null);
        }

        if (step.getThemes() != null && !step.getThemes().isEmpty()) {
            stepDTO.setThemes(
                    step.getThemes()
                            .stream()
                            .map(themeMapper::toDto)
                            .collect(Collectors.toList())
            );
        } else {
            stepDTO.setThemes(List.of());
        }

        return stepDTO;
    }
}