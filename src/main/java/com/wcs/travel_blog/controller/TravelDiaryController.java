package com.wcs.travel_blog.controller;

import com.wcs.travel_blog.dto.UpsertTravelDiaryDto;
import com.wcs.travel_blog.dto.StepRequestDto;
import com.wcs.travel_blog.dto.TravelDiaryDto;
import com.wcs.travel_blog.service.TravelDiaryService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<List<TravelDiaryDto>> getAllDiariesWithStepsAndUser() {
        List<TravelDiaryDto> travelDiaries = travelDiaryService.getAllDiariesWithStepsAndUser();
        if(travelDiaries.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(travelDiaries);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TravelDiaryDto> getTravelDiaryById(@PathVariable Long id) {
        TravelDiaryDto travelDiaryDto = travelDiaryService.getTravelDiaryById(id);
        if(travelDiaryDto == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(travelDiaryDto);
    }

    @PostMapping("/{travelId}/steps")
    public ResponseEntity<TravelDiaryDto> addStepToTravel(@PathVariable Long travelId,
                                                          @RequestBody StepRequestDto dto) {
        TravelDiaryDto travelDiaryDto = travelDiaryService.addStepToTravel(travelId, dto);
        if(travelDiaryDto == null) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(travelDiaryDto);
    }

    @PostMapping
    public ResponseEntity<TravelDiaryDto> createDiary(@RequestBody UpsertTravelDiaryDto dto) {
        TravelDiaryDto travelDiaryDto = travelDiaryService.createDiaryWithFirstStep(dto);
        if(travelDiaryDto == null) {
            return ResponseEntity.badRequest().build();
        }
        return  ResponseEntity.status(HttpStatus.CREATED).body(travelDiaryDto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDiary(@PathVariable Long id) {
        if(travelDiaryService.deleteTravelDiary(id)){
            return ResponseEntity.noContent().build();
        };
        return ResponseEntity.notFound().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<TravelDiaryDto> updateDiary(@PathVariable Long id, @RequestBody UpsertTravelDiaryDto dto) {
        TravelDiaryDto travelDiaryDto = travelDiaryService.updateTravelDiary(id, dto);
        if(travelDiaryDto == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(travelDiaryDto);
    }
}
