package com.wcs.travel_blog.media.service;

import com.wcs.travel_blog.exception.ResourceNotFoundException;
import com.wcs.travel_blog.media.dto.CreateMediaDTO;
import com.wcs.travel_blog.media.dto.MediaDTO;
import com.wcs.travel_blog.media.dto.UpdateMediaDTO;
import com.wcs.travel_blog.media.mapper.MediaMapper;
import com.wcs.travel_blog.media.model.Media;
import com.wcs.travel_blog.media.repository.MediaRepository;
import com.wcs.travel_blog.step.model.Step;
import com.wcs.travel_blog.step.repository.StepRepository;
import com.wcs.travel_blog.travel_diary.model.TravelDiary;
import com.wcs.travel_blog.travel_diary.repository.TravelDiaryRepository;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MediaService {
    private final MediaRepository mediaRepository;
    private final MediaMapper mediaMapper;
    private final TravelDiaryRepository travelDiaryRepository;
    private final StepRepository stepRepository;

    public MediaService(MediaRepository mediaRepository, MediaMapper mediaMapper, TravelDiaryRepository travelDiaryRepository, StepRepository stepRepository) {
        this.mediaRepository = mediaRepository;
        this.mediaMapper = mediaMapper;
        this.travelDiaryRepository = travelDiaryRepository;
        this.stepRepository = stepRepository;
    }

    public List<MediaDTO> getAllMedias() {
        List<Media> medias = mediaRepository.findAll();
        return medias.stream()
                .map(mediaMapper::toDto)
                .collect(Collectors.toList());
    }

    public MediaDTO getMediaById(Long id) {
        Media media = mediaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Le média avec l'id " + id + " n'a pas été trouvé"));
        return mediaMapper.toDto(media);
    }

    public MediaDTO createMedia(CreateMediaDTO dto) {
        Media media = mediaMapper.toEntity(dto);
        media.setCreatedAt(LocalDateTime.now());
        media.setUpdatedAt(LocalDateTime.now());
        Media saved = mediaRepository.save(media);
        return mediaMapper.toDto(saved);
    }

    public MediaDTO updateMedia(Long id, UpdateMediaDTO dto) {
        Media media = mediaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Média non trouvé"));

        if (StringUtils.hasText(dto.getFileUrl()) && !dto.getFileUrl().equals(media.getFileUrl())) {
            media.setFileUrl(dto.getFileUrl());
        }
        if (dto.getMediaType() != null && !dto.getMediaType().equals(media.getMediaType())) {
            media.setMediaType(dto.getMediaType());
        }
        if (dto.getIsVisible() != null && !dto.getIsVisible().equals(media.getIsVisible())) {
            media.setIsVisible(dto.getIsVisible());
        }

        if (dto.getStepId() != null) {
            Step step = stepRepository.findById(dto.getStepId())
                    .orElseThrow(() -> new ResourceNotFoundException("Étape non trouvée"));
            media.setStep(step);
        } else {
            // Si explicitement null dans le DTO, on efface le lien
            media.setStep(null);
        }

        if (dto.getTravelDiaryId() != null) {
            TravelDiary travelDiary = travelDiaryRepository.findById(dto.getTravelDiaryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Carnet non trouvé"));
            media.setTravelDiary(travelDiary);
        } else {
            media.setTravelDiary(null);
        }

        Media saved = mediaRepository.save(media);
        return mediaMapper.toDto(saved);
    }

    public void deleteMediaById(Long id) {
        Media media = mediaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Media non trouvé"));
        mediaRepository.delete(media);
    }

    public List<MediaDTO> getMediaByStep(Long stepId) {
        return mediaRepository.findByStep_Id(stepId).stream()
                .map(mediaMapper::toDto)
                .collect(Collectors.toList());
    }

    public MediaDTO getMediaByTravelDiary(Long diaryId) {
        Media media =  mediaRepository.findByTravelDiary_Id(diaryId)
                .orElseThrow(() -> new ResourceNotFoundException("Aucun média lié au carnet " + diaryId));
        return mediaMapper.toDto(media);
    }
}
