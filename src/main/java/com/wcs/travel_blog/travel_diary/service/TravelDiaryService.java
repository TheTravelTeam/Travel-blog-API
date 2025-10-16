package com.wcs.travel_blog.travel_diary.service;

import com.wcs.travel_blog.media.model.Media;
import com.wcs.travel_blog.media.model.MediaType;
import com.wcs.travel_blog.media.repository.MediaRepository;
import com.wcs.travel_blog.step.model.Step;
import com.wcs.travel_blog.step.repository.StepRepository;
import com.wcs.travel_blog.travel_diary.dto.CreateTravelDiaryDTO;
import com.wcs.travel_blog.travel_diary.dto.TravelDiaryDTO;
import com.wcs.travel_blog.travel_diary.dto.UpdateTravelDiaryDTO;
import com.wcs.travel_blog.travel_diary.dto.UpdateTravelDiaryMediaDTO;
import com.wcs.travel_blog.travel_diary.mapper.TravelDiaryMapper;
import com.wcs.travel_blog.travel_diary.model.TravelDiary;
import com.wcs.travel_blog.travel_diary.repository.TravelDiaryRepository;
import com.wcs.travel_blog.travel_diary.model.TravelStatus;
import com.wcs.travel_blog.user.model.User;
import com.wcs.travel_blog.user.repository.UserRepository;
import com.wcs.travel_blog.util.HtmlSanitizerService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

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
    private  final HtmlSanitizerService htmlSanitizerService;

    public TravelDiaryService(TravelDiaryRepository travelDiaryRepository, TravelDiaryMapper travelDiaryMapper, UserRepository userRepository, MediaRepository mediaRepository, StepRepository stepRepository, HtmlSanitizerService htmlSanitizerService){
        this.travelDiaryRepository=travelDiaryRepository;
        this.travelDiaryMapper=travelDiaryMapper;
        this.userRepository = userRepository;
        this.mediaRepository = mediaRepository;
        this.stepRepository = stepRepository;
        this.htmlSanitizerService = htmlSanitizerService;
    }

    public List<TravelDiaryDTO> getAllTravelDiaries(){
        List<TravelDiary> travelDiaries = travelDiaryRepository.findAllPublishedPublicWithSteps();
        return travelDiaries.stream().map(travelDiaryMapper::toDto).collect(Collectors.toList());
    }

    /**
     * Returns the diaries visible for a given viewer. Admins and owners see every diary (draft/private included),
     * visitors only get the public, published ones.
     */
    public List<TravelDiaryDTO> getTravelDiariesForUser(Long userId, Long currentUserId, boolean isAdmin) {
        boolean isOwner = currentUserId != null && currentUserId.equals(userId);
        boolean canViewAll = isAdmin || isOwner;

        List<TravelDiary> travelDiaries = canViewAll
                ? travelDiaryRepository.findAllByUserIdOrderByUpdatedAtDesc(userId)
                : travelDiaryRepository.findPublishedPublicByUserId(userId);

        return travelDiaries.stream()
                .map(travelDiaryMapper::toDto)
                .collect(Collectors.toList());
    }

    public TravelDiaryDTO getTravelDiaryById(Long id){
        TravelDiary travelDiary=travelDiaryRepository.findById(id).orElseThrow(()-> new NoSuchElementException("Le carnet de voyage avec l'id "+ id + " n'a pas été trouvé"));
        return travelDiaryMapper.toDto(travelDiary);
    }

    public TravelDiaryDTO createTravelDiary(CreateTravelDiaryDTO createTravelDto){
        TravelDiary travelDiary = travelDiaryMapper.toEntity(createTravelDto);

        travelDiary.setTitle(htmlSanitizerService.sanitize(createTravelDto.getTitle()));
        travelDiary.setDescription(htmlSanitizerService.sanitize(createTravelDto.getDescription()));

        travelDiary.setCreatedAt(LocalDateTime.now());
        travelDiary.setUpdatedAt(LocalDateTime.now());

        if (createTravelDto.getUser() != null) {
            User user = userRepository.findById(createTravelDto.getUser())
                    .orElseThrow(() -> new EntityNotFoundException("Utilisateur non trouvé"));
            travelDiary.setUser(user);
        }

        // Media (créé en même temps que le carnet)
        if (createTravelDto.getMedia() != null) {
            Media media = new Media();
            media.setFileUrl(createTravelDto.getMedia().getFileUrl());
            media.setMediaType(createTravelDto.getMedia().getMediaType());
            media.setIsVisible(createTravelDto.getMedia().getIsVisible() != null ? createTravelDto.getMedia().getIsVisible() : Boolean.TRUE);

            // liaison bidirectionnelle
            media.setTravelDiary(travelDiary);
            travelDiary.setMedia(media);
        }

        if (createTravelDto.getSteps() != null && !createTravelDto.getSteps().isEmpty()) {
            List<Step> steps = stepRepository.findAllById(createTravelDto.getSteps());

            if (steps.size() != createTravelDto.getSteps().size()) {
                throw new EntityNotFoundException("Un ou plusieurs étapes n'ont pas été trouvées");
            }

            travelDiary.setSteps(steps);
        }

        if (travelDiary.getIsPrivate() == null) {
            travelDiary.setIsPrivate(Boolean.FALSE);
        }

        if (CollectionUtils.isEmpty(travelDiary.getSteps()) || travelDiary.getIsPublished() == null) {
            travelDiary.setIsPublished(Boolean.FALSE);
        }

        if (travelDiary.getStatus() == null) {
            travelDiary.setStatus(resolveDiaryStatus(travelDiary));
        }

        alignStepsWithDiaryStatus(travelDiary);

        TravelDiary travelDiaryToSaved= travelDiaryRepository.save(travelDiary);

        return travelDiaryMapper.toDto(travelDiaryToSaved);
    }

   public TravelDiaryDTO updateTravelDiary(Long id, UpdateTravelDiaryDTO updateTravelDiaryResponse) {
       TravelDiary travelDiary = travelDiaryRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Carnet de voyage non trouvé"));

       if (updateTravelDiaryResponse.getTitle() != null && !updateTravelDiaryResponse.getTitle().equals(travelDiary.getTitle())) {
           travelDiary.setTitle(htmlSanitizerService.sanitize(updateTravelDiaryResponse.getTitle()));
       }

       if (updateTravelDiaryResponse.getDescription() != null && !updateTravelDiaryResponse.getDescription().equals(travelDiary.getDescription())) {
           travelDiary.setDescription(htmlSanitizerService.sanitize(updateTravelDiaryResponse.getDescription()));
       }

       if (updateTravelDiaryResponse.getIsPrivate() != null && !updateTravelDiaryResponse.getIsPrivate().equals(travelDiary.getIsPrivate())) {
           travelDiary.setIsPrivate(updateTravelDiaryResponse.getIsPrivate());
       }

       if (travelDiary.getIsPrivate() == null) {
           travelDiary.setIsPrivate(Boolean.FALSE);
       }

       if (updateTravelDiaryResponse.getIsPublished() != null && !updateTravelDiaryResponse.getIsPublished().equals(travelDiary.getIsPublished())) {
           travelDiary.setIsPublished(updateTravelDiaryResponse.getIsPublished());
       }

       if (updateTravelDiaryResponse.getStatus() != null && !updateTravelDiaryResponse.getStatus().equals(travelDiary.getStatus())) {
           travelDiary.setStatus(updateTravelDiaryResponse.getStatus());
       }

       if (updateTravelDiaryResponse.getStartDate() != null) {
           travelDiary.setStartDate(updateTravelDiaryResponse.getStartDate());
       }

       if (updateTravelDiaryResponse.getEndDate() != null) {
           travelDiary.setEndDate(updateTravelDiaryResponse.getEndDate());
       }

       if (updateTravelDiaryResponse.getStatus() == null && travelDiary.getStatus() != TravelStatus.DISABLED) {
           travelDiary.setStatus(resolveDiaryStatus(travelDiary));
       }

       alignStepsWithDiaryStatus(travelDiary);

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

       handleMediaUpdate(updateTravelDiaryResponse.getMedia(), travelDiary);

       if (updateTravelDiaryResponse.getSteps() != null) {
           if (!updateTravelDiaryResponse.getSteps().isEmpty()) {
               List<Step> steps = stepRepository.findAllById(updateTravelDiaryResponse.getSteps());
               if (steps.size() != updateTravelDiaryResponse.getSteps().size()) {
                   throw new EntityNotFoundException("Quelques étapes n'ont pas été trouvé");
               }
               travelDiary.setSteps(steps);
           } else if (travelDiary.getSteps() != null) {
               travelDiary.getSteps().clear();
           }
       }

       if (CollectionUtils.isEmpty(travelDiary.getSteps()) || travelDiary.getIsPublished() == null) {
           travelDiary.setIsPublished(Boolean.FALSE);
       }




       TravelDiary updated = travelDiaryRepository.save(travelDiary);
       return travelDiaryMapper.toDto(updated);
   }

   public void deleteTravelDiary(Long id){
        TravelDiary travelDiary=travelDiaryRepository.findById(id).orElseThrow(()->new EntityNotFoundException("Carnet de voyage Non trouvé"));
        travelDiaryRepository.delete(travelDiary);
   }

   private void handleMediaUpdate(UpdateTravelDiaryMediaDTO mediaDto, TravelDiary travelDiary) {
       if (mediaDto == null) {
           return;
       }

       if (mediaDto.getId() != null) {
           Media existing = mediaRepository.findById(mediaDto.getId())
                   .orElseThrow(() -> new EntityNotFoundException("Média non trouvé"));
           travelDiary.setMedia(existing);
           return;
       }

       if (!StringUtils.hasText(mediaDto.getFileUrl())) {
           travelDiary.setMedia(null);
           return;
       }

       Media media = new Media();
       media.setFileUrl(mediaDto.getFileUrl());
       media.setMediaType(mediaDto.getMediaType() != null ? mediaDto.getMediaType() : MediaType.PHOTO);
       media.setIsVisible(mediaDto.getIsVisible() != null ? mediaDto.getIsVisible() : Boolean.TRUE);
       media.setPublicId(mediaDto.getPublicId());
       media.setTravelDiary(travelDiary);

       Media saved = mediaRepository.save(media);
       travelDiary.setMedia(saved);
   }

   private void alignStepsWithDiaryStatus(TravelDiary travelDiary) {
       if (travelDiary.getStatus() != TravelStatus.DISABLED || CollectionUtils.isEmpty(travelDiary.getSteps())) {
           return;
       }

       travelDiary.getSteps().forEach(step -> step.setStatus(TravelStatus.DISABLED));
   }

   private TravelStatus resolveDiaryStatus(TravelDiary travelDiary) {
       return travelDiary.getEndDate() != null ? TravelStatus.COMPLETED : TravelStatus.IN_PROGRESS;
   }

}
