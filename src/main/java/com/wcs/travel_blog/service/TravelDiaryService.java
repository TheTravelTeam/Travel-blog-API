package com.wcs.travel_blog.service;

import com.wcs.travel_blog.dto.UpsertTravelDiaryDto;
import com.wcs.travel_blog.dto.StepRequestDto;
import com.wcs.travel_blog.dto.TravelDiaryDto;
import com.wcs.travel_blog.exception.ResourceNotFoundException;
import com.wcs.travel_blog.mapper.TravelDiaryMapper;
import com.wcs.travel_blog.model.Step;
import com.wcs.travel_blog.model.TravelDiary;
import com.wcs.travel_blog.repository.TravelDiaryRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TravelDiaryService {
    private final TravelDiaryRepository travelDiaryRepository;
    private final TravelDiaryMapper travelDiaryMapper;

    public TravelDiaryService(TravelDiaryRepository travelDiaryRepository,
                              TravelDiaryMapper travelDiaryMapper) {
        this.travelDiaryRepository = travelDiaryRepository;
        this.travelDiaryMapper = travelDiaryMapper;
    }

    public List<TravelDiaryDto> getAllDiariesWithStepsAndUser() {
        List<TravelDiary> travelDiaries = travelDiaryRepository.findAll();
        if(travelDiaries.isEmpty()){
            throw new ResourceNotFoundException("Aucun Journal trouvé");
        }
        return travelDiaries.stream()
                .map(travelDiaryMapper::mapTravelDiaryToDto)
                .collect(Collectors.toList());
    }

    public TravelDiaryDto getTravelDiaryById(Long id) {
        TravelDiary travelDiary = travelDiaryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Le journal avec l'id " + id + " n'a pas été trouvé"));;
        return travelDiaryMapper.mapTravelDiaryToDto(travelDiary);
    }

    public TravelDiaryDto addStepToTravel(Long travelId, StepRequestDto dto) {
        TravelDiary travel = travelDiaryRepository.findById(travelId)
                .orElseThrow(() ->  new ResourceNotFoundException("Le journal avec l'id " + travelId + " n'a pas été trouvé"));

        Step step = travelDiaryMapper.createStepFromStepRequestDto(dto, travel);

        travel.getSteps().add(step);
        travelDiaryRepository.save(travel);
        return travelDiaryMapper.mapTravelDiaryToDto(travel);
    }

    public TravelDiaryDto createDiaryWithFirstStep(UpsertTravelDiaryDto dto) {
       TravelDiary travelDiary = travelDiaryMapper.createTravelDiaryFromCreateTravelDiaryDto(dto);
       Step firstStep = travelDiaryMapper.createFirstStepFromCreateTravelDiaryDto(dto, travelDiary);

        travelDiary.getSteps().add(firstStep);

        TravelDiary savedDiary = travelDiaryRepository.save(travelDiary);
        return travelDiaryMapper.mapTravelDiaryToDto(savedDiary);
    }

    public boolean deleteTravelDiary(Long id) {
        TravelDiary travelDiary = travelDiaryRepository.findById(id)
                .orElseThrow(()->new ResourceNotFoundException("Impossible de supprimé le diary avec l'id : " + id ));

        travelDiaryRepository.delete(travelDiary);
        return true;
    }

    public TravelDiaryDto updateTravelDiary(Long id, UpsertTravelDiaryDto dto) {
        TravelDiary diary = travelDiaryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Le journal avec l'id " + id + " n'a pas été trouvé"));

        // Modifie directement en memoire le diary -- pas besoin de le stocker dans une variable intermédiaire
        travelDiaryMapper.updateDiaryFromUpsetDiaryDto(dto, diary);

        TravelDiary updated = travelDiaryRepository.save(diary);
        return travelDiaryMapper.mapTravelDiaryToDto(updated);
    }


}
