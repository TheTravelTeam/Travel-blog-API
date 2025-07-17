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

    public TravelDiary getTravelDiaryById(Long id){
        return travelDiaryRepository.findById(id).orElseThrow(()-> new NoSuchElementException("Le carnet de voyage avec l'id "+ id + " n'a pas été trouvé"));
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

   public TravelDiaryDTO updateTravelDiary(Long id, UpdateTravelDiaryDTO updateTravelDiaryDTO) {
       TravelDiary travelDiary = travelDiaryRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Carnet de voyage non trouvé"));

       travelDiary.setTitle(updateTravelDiaryDTO.getTitle());
       travelDiary.setDescription(updateTravelDiaryDTO.getDescription());
       travelDiary.setIsPrivate(updateTravelDiaryDTO.getIsPrivate());
       travelDiary.setIsPublished(updateTravelDiaryDTO.getIsPublished());
       travelDiary.setStatus(updateTravelDiaryDTO.getStatus());
       travelDiary.setCanComment(updateTravelDiaryDTO.getCanComment());
       travelDiary.setLatitude(updateTravelDiaryDTO.getLatitude());
       travelDiary.setLongitude(updateTravelDiaryDTO.getLongitude());
       travelDiary.setUpdatedAt(LocalDateTime.now());

       if (updateTravelDiaryDTO.getUser() != null) {
           User user = userRepository.findById(updateTravelDiaryDTO.getUser()).orElseThrow(() -> new EntityNotFoundException("Utilisateur non trouvé"));
           travelDiary.setUser(user);
       } else{
           throw new IllegalArgumentException("L'utilisateur est obligatoire pour un carnet de voyage");
       }

       if(updateTravelDiaryDTO.getMedia()!=null){
           Media media = mediaRepository.findById(updateTravelDiaryDTO.getMedia()).orElseThrow(()-> new EntityNotFoundException("Média non trouvé"));
           travelDiary.setMedia(media);
       } else {
           travelDiary.setMedia(null);
       }

       if(updateTravelDiaryDTO.getSteps()!=null && !updateTravelDiaryDTO.getSteps().isEmpty()){
            List<Step>steps=stepRepository.findAllById(updateTravelDiaryDTO.getSteps());
                if(steps.size()!=updateTravelDiaryDTO.getSteps().size()){
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
