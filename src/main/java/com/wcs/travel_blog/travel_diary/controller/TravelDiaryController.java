package com.wcs.travel_blog.travel_diary.controller;

import com.wcs.travel_blog.travel_diary.dto.CreateTravelDiaryDTO;
import com.wcs.travel_blog.travel_diary.dto.TravelDiaryDTO;
import com.wcs.travel_blog.travel_diary.dto.UpdateTravelDiaryDTO;
import com.wcs.travel_blog.travel_diary.service.TravelDiaryService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/travel-diaries")
public class TravelDiaryController {

    private final TravelDiaryService travelDiaryService;

    public TravelDiaryController(TravelDiaryService travelDiaryService) {
        this.travelDiaryService = travelDiaryService;
    }

    @GetMapping
    public ResponseEntity<List<TravelDiaryDTO>> getAllTravelDiaries(){
        List<TravelDiaryDTO> travelDiaries = travelDiaryService.getAllTravelDiaries();
        if(travelDiaries.isEmpty()){
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(travelDiaries);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TravelDiaryDTO> getTravelDiaryById(@PathVariable Long id){
        TravelDiaryDTO travelDiary = travelDiaryService.getTravelDiaryById(id);
        if(travelDiary==null){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(travelDiary);
    }

    @PostMapping
    public ResponseEntity<TravelDiaryDTO> createTravelDiary(@Valid @RequestBody CreateTravelDiaryDTO createTravelDiaryRequest){

        TravelDiaryDTO travelDiaryResponse = travelDiaryService.createTravelDiary(createTravelDiaryRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(travelDiaryResponse);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TravelDiaryDTO> updateTravelDiary(@PathVariable Long id , @RequestBody UpdateTravelDiaryDTO updateTravelDiaryRequest){
        TravelDiaryDTO updateTravelDiaryResponse = travelDiaryService.updateTravelDiary(id,updateTravelDiaryRequest);
        if(updateTravelDiaryResponse==null){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(updateTravelDiaryResponse);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteTravelDiary(@PathVariable Long id){
        travelDiaryService.deleteTravelDiary(id);
        return ResponseEntity.ok("Carnet de voyage supprimé avec succès");
    }


}
