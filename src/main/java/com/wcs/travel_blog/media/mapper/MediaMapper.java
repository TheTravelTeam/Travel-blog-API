package com.wcs.travel_blog.media.mapper;

import com.wcs.travel_blog.exception.ResourceNotFoundException;
import com.wcs.travel_blog.media.dto.CreateMediaDTO;
import com.wcs.travel_blog.media.dto.MediaDTO;
import com.wcs.travel_blog.media.model.Media;
import com.wcs.travel_blog.step.model.Step;
import com.wcs.travel_blog.step.repository.StepRepository;
import com.wcs.travel_blog.travel_diary.model.TravelDiary;
import com.wcs.travel_blog.travel_diary.repository.TravelDiaryRepository;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class MediaMapper {
    private final StepRepository stepRepository;
    private final TravelDiaryRepository travelDiaryRepository;

    public MediaMapper(StepRepository stepRepository, TravelDiaryRepository travelDiaryRepository){
        this.stepRepository = stepRepository;
        this.travelDiaryRepository = travelDiaryRepository;
    }

    public MediaDTO toDto (Media media){
        if (media == null) return null;

        MediaDTO dto = new MediaDTO();
        Boolean isVisible = media.getIsVisible() != null
                ? media.getIsVisible() : Boolean.TRUE;
        Long stepId = media.getStep() != null
                ? media.getStep().getId()
                : null;
        Long diaryId = media.getTravelDiary() != null
                ? media.getTravelDiary().getId()
                : null;

        dto.setId(media.getId());
        dto.setFileUrl(media.getFileUrl());
        dto.setMediaType(media.getMediaType());
        dto.setIsVisible(isVisible);
        if (!Objects.equals(dto.getStepId(), media.getStep() != null ? media.getStep().getId() : null)) {
            dto.setStepId(stepId);
        }

        dto.setTravelDiaryId(diaryId);
        dto.setCreatedAt(media.getCreatedAt());
        dto.setUpdatedAt(media.getUpdatedAt());
        return dto;
    }

    public Media toEntityFromCreate(CreateMediaDTO dto){
        Media media = new Media();

        media.setFileUrl(dto.getFileUrl());
        media.setMediaType(dto.getMediaType());
        media.setIsVisible(dto.getIsVisible() != null ? dto.getIsVisible() : Boolean.TRUE);

        if(dto.getStepId() != null){
            Step step = stepRepository.findById(dto.getStepId())
                    .orElseThrow(() -> new ResourceNotFoundException("Etape non trouvée"));
            media.setStep(step);
        }

        if(dto.getTravelDiaryId() != null){
            TravelDiary travelDiary =
                    travelDiaryRepository.findById(dto.getTravelDiaryId()).orElseThrow(() -> new ResourceNotFoundException("Carnet non trouvé"));
            media.setTravelDiary(travelDiary);
        }

        return media;

    }
    // Création depuis MediaDTO (utile pour PUT Step)
    public Media toEntityFromDto(MediaDTO mediaDTO) {
        Media media = new Media();
        media.setId(mediaDTO.getId()); // peut être null pour nouvel objet
        media.setFileUrl(mediaDTO.getFileUrl());
        media.setMediaType(mediaDTO.getMediaType());
        media.setIsVisible(mediaDTO.getIsVisible() != null ? mediaDTO.getIsVisible() : Boolean.TRUE);

        if (mediaDTO.getStepId() != null) {
            Step step = stepRepository.findById(mediaDTO.getStepId())
                    .orElseThrow(() -> new ResourceNotFoundException("Etape non trouvée"));
            media.setStep(step);
        }

        if (mediaDTO.getTravelDiaryId() != null) {
            TravelDiary travelDiary = travelDiaryRepository.findById(mediaDTO.getTravelDiaryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Carnet non trouvé"));
            media.setTravelDiary(travelDiary);
        }

        return media;
    }

    // Mise à jour d’un média existant
    public void updateEntity(Media mediaEntity, MediaDTO mediaDTO) {
        if (mediaDTO.getFileUrl() != null) mediaEntity.setFileUrl(mediaDTO.getFileUrl());
        if (mediaDTO.getMediaType() != null) mediaEntity.setMediaType(mediaDTO.getMediaType());
        if (mediaDTO.getIsVisible() != null) mediaEntity.setIsVisible(mediaDTO.getIsVisible());

        if (mediaDTO.getStepId() != null) {
            Step step = stepRepository.findById(mediaDTO.getStepId())
                    .orElseThrow(() -> new ResourceNotFoundException("Etape non trouvée"));
            mediaEntity.setStep(step);
        }

        if (mediaDTO.getTravelDiaryId() != null) {
            TravelDiary travelDiary = travelDiaryRepository.findById(mediaDTO.getTravelDiaryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Carnet non trouvé"));
            mediaEntity.setTravelDiary(travelDiary);
        }
    }
}
