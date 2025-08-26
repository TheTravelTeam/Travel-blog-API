package com.wcs.travel_blog.travel_diary.service;

import com.wcs.travel_blog.media.model.Media;
import com.wcs.travel_blog.media.repository.MediaRepository;
import com.wcs.travel_blog.step.model.Step;
import com.wcs.travel_blog.step.repository.StepRepository;
import com.wcs.travel_blog.travel_diary.dto.CreateTravelDiaryDTO;
import com.wcs.travel_blog.travel_diary.dto.TravelDiaryDTO;
import com.wcs.travel_blog.travel_diary.dto.UpdateTravelDiaryDTO;
import com.wcs.travel_blog.travel_diary.mapper.TravelDiaryMapper;
import com.wcs.travel_blog.travel_diary.model.TravelDiary;
import com.wcs.travel_blog.travel_diary.repository.TravelDiaryRepository;
import com.wcs.travel_blog.user.model.User;
import com.wcs.travel_blog.user.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
public class TravelDiaryService {

    private final TravelDiaryRepository travelDiaryRepository;
    private final TravelDiaryMapper travelDiaryMapper;
    private final UserRepository userRepository;
    private final MediaRepository mediaRepository;
    private final StepRepository stepRepository;

    public TravelDiaryService(TravelDiaryRepository travelDiaryRepository, TravelDiaryMapper travelDiaryMapper, UserRepository userRepository, MediaRepository mediaRepository, StepRepository stepRepository){
        this.travelDiaryRepository=travelDiaryRepository;
        this.travelDiaryMapper=travelDiaryMapper;
        this.userRepository = userRepository;
        this.mediaRepository = mediaRepository;
        this.stepRepository = stepRepository;
    }

    public List<TravelDiaryDTO> getAllTravelDiaries(){
        List<TravelDiary> travelDiaries = travelDiaryRepository.findAll();
        return travelDiaries.stream().map(travelDiaryMapper::toDto).collect(Collectors.toList());
    }

    public TravelDiaryDTO getTravelDiaryById(Long id){
        TravelDiary travelDiary=travelDiaryRepository.findById(id).orElseThrow(()-> new NoSuchElementException("Le carnet de voyage avec l'id "+ id + " n'a pas été trouvé"));
        return travelDiaryMapper.toDto(travelDiary);
    }

    public TravelDiaryDTO createTravelDiary(CreateTravelDiaryDTO createTravelDto){
        TravelDiary travelDiary = travelDiaryMapper.toEntity(createTravelDto);

        travelDiary.setCreatedAt(LocalDateTime.now());
        travelDiary.setUpdatedAt(LocalDateTime.now());

        if (createTravelDto.getUser() != null) {
            User user = userRepository.findById(createTravelDto.getUser())
                    .orElseThrow(() -> new EntityNotFoundException("Utilisateur non trouvé"));
            travelDiary.setUser(user);
        }

        if (createTravelDto.getMedia() != null) {
            Media media = mediaRepository.findById(createTravelDto.getMedia())
                    .orElseThrow(() -> new EntityNotFoundException("Média non trouvé"));
            travelDiary.setMedia(media);
        }

        if (createTravelDto.getSteps() != null && !createTravelDto.getSteps().isEmpty()) {
            List<Step> steps = stepRepository.findAllById(createTravelDto.getSteps());

            if (steps.size() != createTravelDto.getSteps().size()) {
                throw new EntityNotFoundException("Un ou plusieurs étapes n'ont pas été trouvées");
            }

            travelDiary.setSteps(steps);
        }

        TravelDiary travelDiaryToSaved= travelDiaryRepository.save(travelDiary);

        return travelDiaryMapper.toDto(travelDiaryToSaved);
    }

   public TravelDiaryDTO updateTravelDiary(Long id, UpdateTravelDiaryDTO updateTravelDiaryResponse) {
       TravelDiary travelDiary = travelDiaryRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Carnet de voyage non trouvé"));

       if (updateTravelDiaryResponse.getTitle() != null && !updateTravelDiaryResponse.getTitle().equals(travelDiary.getTitle())) {
           travelDiary.setTitle(updateTravelDiaryResponse.getTitle());
       }

       if (updateTravelDiaryResponse.getDescription() != null && !updateTravelDiaryResponse.getDescription().equals(travelDiary.getDescription())) {
           travelDiary.setDescription(updateTravelDiaryResponse.getDescription());
       }

       if (updateTravelDiaryResponse.getIsPrivate() != null && !updateTravelDiaryResponse.getIsPrivate().equals(travelDiary.getIsPrivate())) {
           travelDiary.setIsPrivate(updateTravelDiaryResponse.getIsPrivate());
       }

       if (updateTravelDiaryResponse.getIsPublished() != null && !updateTravelDiaryResponse.getIsPublished().equals(travelDiary.getIsPublished())) {
           travelDiary.setIsPublished(updateTravelDiaryResponse.getIsPublished());
       }

       if (updateTravelDiaryResponse.getStatus() != null && !updateTravelDiaryResponse.getStatus().equals(travelDiary.getStatus())) {
           travelDiary.setStatus(updateTravelDiaryResponse.getStatus());
       }

       if (updateTravelDiaryResponse.getCanComment() != null && !updateTravelDiaryResponse.getCanComment().equals(travelDiary.getCanComment())) {
           travelDiary.setCanComment(updateTravelDiaryResponse.getCanComment());
       }

       if (updateTravelDiaryResponse.getLatitude() != null && !updateTravelDiaryResponse.getLatitude().equals(travelDiary.getLatitude())) {
           travelDiary.setLatitude(updateTravelDiaryResponse.getLatitude());
       }

       if (updateTravelDiaryResponse.getLongitude() != null && !updateTravelDiaryResponse.getLongitude().equals(travelDiary.getLongitude())) {
           travelDiary.setLongitude(updateTravelDiaryResponse.getLongitude());
       }
       travelDiary.setUpdatedAt(LocalDateTime.now());

       if (updateTravelDiaryResponse.getUser() != null) {
           User user = userRepository.findById(updateTravelDiaryResponse.getUser()).orElseThrow(() -> new EntityNotFoundException("Utilisateur non trouvé"));
           travelDiary.setUser(user);
       }

       if(updateTravelDiaryResponse.getMedia()!=null){
           Media media = mediaRepository.findById(updateTravelDiaryResponse.getMedia()).orElseThrow(()-> new EntityNotFoundException("Média non trouvé"));
           travelDiary.setMedia(media);
       } else {
           travelDiary.setMedia(null);
       }

       if(updateTravelDiaryResponse.getSteps()!=null && !updateTravelDiaryResponse.getSteps().isEmpty()){
            List<Step>steps=stepRepository.findAllById(updateTravelDiaryResponse.getSteps());
                if(steps.size()!=updateTravelDiaryResponse.getSteps().size()){
                    throw new EntityNotFoundException("Quelques étapes n'ont pas été trouvé");
                }
                travelDiary.setSteps(steps);
       } else {
           travelDiary.getSteps().clear();
       }

       TravelDiary updated = travelDiaryRepository.save(travelDiary);
       return travelDiaryMapper.toDto(updated);
   }

   public void deleteTravelDiary(Long id){
        TravelDiary travelDiary=travelDiaryRepository.findById(id).orElseThrow(()->new EntityNotFoundException("Carnet de voyage Non trouvé"));
        travelDiaryRepository.delete(travelDiary);
   }
}
