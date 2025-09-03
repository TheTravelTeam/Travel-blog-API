package com.wcs.travel_blog.step.service;

import com.wcs.travel_blog.exception.ResourceNotFoundException;
import com.wcs.travel_blog.media.dto.MediaDTO;
import com.wcs.travel_blog.media.mapper.MediaMapper;
import com.wcs.travel_blog.media.model.Media;
import com.wcs.travel_blog.step.dto.StepDTO;
import com.wcs.travel_blog.step.mapper.StepMapper;
import com.wcs.travel_blog.step.model.Step;
import com.wcs.travel_blog.step.repository.StepRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class StepService {
    private final StepRepository stepRepository;
    private final StepMapper stepMapper;
    private final MediaMapper mediaMapper;

    public StepService(StepRepository stepRepository, StepMapper stepMapper, MediaMapper mediaMapper) {
        this.stepRepository = stepRepository;
        this.stepMapper = stepMapper;
        this.mediaMapper = mediaMapper;
    }

    public List<StepDTO> getAllSteps() {
        List<Step> steps = stepRepository.findAll();
        return steps.stream().map(stepMapper::toDto).collect(Collectors.toList());
    }

    public StepDTO getStepById(Long stepId) {
        Step step = stepRepository.findById(stepId).orElseThrow(() -> new ResourceNotFoundException("Step not found with id: " + stepId));
        return stepMapper.toDto(step);
    }

    public StepDTO createStep(StepDTO stepDto) {
        Step step = stepMapper.toEntity(stepDto);
        step.setCreatedAt(LocalDateTime.now());
        step.setUpdatedAt(LocalDateTime.now());
        Step savedStep = stepRepository.save(step);
        return stepMapper.toDto(savedStep);
    }

    public StepDTO updateStep(Long stepId, StepDTO stepDto) {
        Step existingStep = stepRepository.findById(stepId)
                .orElseThrow(() -> new ResourceNotFoundException("Step not found with id: " + stepId));

        existingStep.setTitle(stepDto.getTitle());
        existingStep.setDescription(stepDto.getDescription());
        existingStep.setStartDate(stepDto.getStartDate());
        existingStep.setEndDate(stepDto.getEndDate());
        existingStep.setStatus(stepDto.getStatus());
        existingStep.setLatitude(stepDto.getLatitude());
        existingStep.setLongitude(stepDto.getLongitude());
        existingStep.setCity(stepDto.getCity());
        existingStep.setCountry(stepDto.getCountry());
        existingStep.setContinent(stepDto.getContinent());
        existingStep.setUpdatedAt(LocalDateTime.now());

        if(stepDto.getMedia() != null) {
            if(existingStep.getMedias() == null) {
                existingStep.setMedias(new ArrayList<>());
            }

            for (MediaDTO mediaDto : stepDto.getMedia()) {
                if (mediaDto.getId() == null) {
                    // Nouveau média
                    Media newMedia = mediaMapper.toEntityFromDto(mediaDto);
                    newMedia.setStep(existingStep); // associer au step
                    existingStep.getMedias().add(newMedia);
                } else {
                    // Mise à jour d’un média existant
                    Media mediaEntity = existingStep.getMedias().stream()
                            .filter(media -> media.getId().equals(mediaDto.getId()))
                            .findFirst()
                            .orElse(null);

                    if (mediaEntity != null) {
                        mediaMapper.updateEntity(mediaEntity, mediaDto);
                    }
                }
            }
        }

        Step updatedStep = stepRepository.save(existingStep);
        return stepMapper.toDto(updatedStep);
    }

    public void deleteStep(Long stepId) {
        Step existingStep = stepRepository.findById(stepId)
                .orElseThrow(() -> new ResourceNotFoundException("Step not found with id: " + stepId));
        stepRepository.delete(existingStep);
    }

}
