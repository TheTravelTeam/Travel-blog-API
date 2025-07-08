package com.wcs.travel_blog.service;

import com.wcs.travel_blog.dto.StepRequestDto;
import com.wcs.travel_blog.model.Step;
import com.wcs.travel_blog.model.TravelDiary;
import com.wcs.travel_blog.repository.StepRepository;
import com.wcs.travel_blog.repository.TravelDiaryRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

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
}
