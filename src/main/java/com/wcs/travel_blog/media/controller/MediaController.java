package com.wcs.travel_blog.media.controller;

import com.wcs.travel_blog.media.dto.CreateMediaDTO;
import com.wcs.travel_blog.media.dto.MediaDTO;
import com.wcs.travel_blog.media.dto.UpdateMediaDTO;
import com.wcs.travel_blog.media.service.MediaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/medias")
public class MediaController {

    private final MediaService mediaService;

    public MediaController(MediaService mediaService) {
        this.mediaService = mediaService;
    }


    @GetMapping
    public ResponseEntity<List<MediaDTO>> getAllMedias() {
        List<MediaDTO> medias = mediaService.getAllMedias();
        if (medias.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(medias);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MediaDTO> getMediaById(@PathVariable Long id) {
        MediaDTO media = mediaService.getMediaById(id);
        if (media == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(media);
    }

    @PostMapping
    public ResponseEntity<MediaDTO> createMedia(@Valid @RequestBody CreateMediaDTO createMediaDTO) {
        MediaDTO created = mediaService.createMedia(createMediaDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<MediaDTO> updateMedia(@PathVariable Long id,
                                                @Valid @RequestBody UpdateMediaDTO updateMediaDTO) {
        MediaDTO updated = mediaService.updateMedia(id, updateMediaDTO);
        if (updated == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteMedia(@PathVariable Long id) {
        mediaService.deleteMediaById(id);
        return ResponseEntity.ok("Média supprimé avec succès");
    }

    @GetMapping("/step/{stepId}")
    public ResponseEntity<List<MediaDTO>> getMediasByStep(@PathVariable Long stepId) {
        List<MediaDTO> medias = mediaService.getMediaByStep(stepId);
        if (medias.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(medias);
    }

    @GetMapping("/travel-diary/{diaryId}")
    public ResponseEntity<MediaDTO> getMediaByTravelDiary(@PathVariable Long diaryId) {
        MediaDTO media = mediaService.getMediaByTravelDiary(diaryId);
        if (media == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(media);
    }
}
