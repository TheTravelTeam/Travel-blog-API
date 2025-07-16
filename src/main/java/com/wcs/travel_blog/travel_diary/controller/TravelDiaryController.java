package com.wcs.travel_blog.travel_diary.controller;

import com.wcs.travel_blog.travel_diary.model.TravelDiary;
import com.wcs.travel_blog.travel_diary.repository.TravelDiaryRepository;
import com.wcs.travel_blog.travel_diary.service.TravelDiaryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/travelDiary")
public class TravelDiaryController {

    private final TravelDiaryService travelDiaryService;

    public TravelDiaryController(TravelDiaryRepository travelDiaryRepository) {
        this.travelDiaryService = travelDiaryService;
    }

    @GetMapping
    public ResponseEntity<List<TravelDiary>> getAllTravelDiaries(){
        List<TravelDiary> travelDiaries=travelDiaryService.getAllTravelDiaries();
        if(travelDiaries.isEmpty()){
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(travelDiaries);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TravelDiary> getTravelDiaryById(@PathVariable Long id){
        TravelDiary travelDiary = travelDiaryService.findById(id);
        if(travelDiary==null){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(travelDiary);
    }
}
