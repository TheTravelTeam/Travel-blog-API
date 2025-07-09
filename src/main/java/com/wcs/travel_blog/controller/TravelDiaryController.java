package com.wcs.travel_blog.controller;

import com.wcs.travel_blog.dto.CreateTravelDiaryDto;
import com.wcs.travel_blog.dto.StepRequestDto;
import com.wcs.travel_blog.model.TravelDiary;
import com.wcs.travel_blog.repository.TravelDiaryRepository;
import com.wcs.travel_blog.service.TravelDiaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/travels-diaries")
public class TravelDiaryController {

    private final TravelDiaryService travelDiaryService;

    public TravelDiaryController(TravelDiaryService travelDiaryService) {
        this.travelDiaryService = travelDiaryService;
    }

    @GetMapping
    public List<TravelDiary> getAll() {
        return travelDiaryService.getAllDiariesWithStepsAndUser();
    }

    @GetMapping("/{id}")
    public TravelDiary getById(@PathVariable Long id) {
        return travelDiaryService.getTravelDiaryById(id);
    }

    @PostMapping("/{travelId}/steps")
    public TravelDiary addStepToTravel(@PathVariable Long travelId, @RequestBody StepRequestDto dto) {
        return travelDiaryService.addStepToTravel(travelId, dto);
    }


    @PostMapping
    public TravelDiary createDiary(@RequestBody CreateTravelDiaryDto dto) {
        return travelDiaryService.createDiaryWithFirstStep(dto);
    }
}
