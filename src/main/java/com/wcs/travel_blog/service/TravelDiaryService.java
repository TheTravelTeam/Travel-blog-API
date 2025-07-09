package com.wcs.travel_blog.service;

import com.wcs.travel_blog.dto.CreateTravelDiaryDto;
import com.wcs.travel_blog.dto.StepRequestDto;
import com.wcs.travel_blog.model.Media;
import com.wcs.travel_blog.model.Step;
import com.wcs.travel_blog.model.TravelDiary;
import com.wcs.travel_blog.repository.StepRepository;
import com.wcs.travel_blog.repository.TravelDiaryRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static com.wcs.travel_blog.model.Step.Status.*;

@Service
public class TravelDiaryService {
    private final TravelDiaryRepository travelDiaryRepository;
    private final StepRepository stepRepository;

    public TravelDiaryService(TravelDiaryRepository travelDiaryRepository, StepRepository stepRepository) {
        this.travelDiaryRepository = travelDiaryRepository;
        this.stepRepository = stepRepository;
    }

    public List<TravelDiary> getAllDiariesWithStepsAndUser() {
        return travelDiaryRepository.findAll();
    }

    public TravelDiary getTravelDiaryById(Long id) {
        return travelDiaryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Diary not found"));
    }

    public TravelDiary addStepToTravel(Long travelId, StepRequestDto dto) {
        TravelDiary travel = travelDiaryRepository.findById(travelId)
                .orElseThrow(() -> new RuntimeException("Travel not found"));

        Step step = new Step();
        step.setTitle(dto.title());
        step.setDescription(dto.description());
        step.setLatitude(dto.latitude());
        step.setLongitude(dto.longitude());
        step.setTravelDiary(travel);

        travel.getSteps().add(step);
        travelDiaryRepository.save(travel); // cascade persist si bien configuré
        return travel;
    }

    public TravelDiary createDiaryWithFirstStep(CreateTravelDiaryDto dto) {
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


        // Si coverMedia envoyé
        if (dto.getCoverMedia() != null) {
            Media media = new Media();
            media.setFileUrl(dto.getCoverMedia().getFileUrl());
            media.setMediaType(Media.MediaType.valueOf(dto.getCoverMedia().getMediaType()));
            media.setCreatedAt(LocalDateTime.now());
            media.setUpdatedAt(LocalDateTime.now());
            media.setStatus(Media.Status.VISIBLE);

            diary.setCoverMedia(media);
        }

        // Création de la première étape (point de départ)
        Step firstStep = new Step();
        firstStep.setTitle("Départ");
        firstStep.setDescription("Point de départ");
        firstStep.setLatitude(dto.getLatitude());
        firstStep.setLongitude(dto.getLongitude());
        firstStep.setCreatedAt(LocalDateTime.now());
        firstStep.setUpdatedAt(LocalDateTime.now());
        firstStep.setStatus(COMPLETED);
        firstStep.setTravelDiary(diary);

        diary.getSteps().add(firstStep);

        // Sauvegarde (attention : save cascade sur steps doit être activé)
        return travelDiaryRepository.save(diary);
    }
}
