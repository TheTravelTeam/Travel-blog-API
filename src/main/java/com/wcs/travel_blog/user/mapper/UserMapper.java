package com.wcs.travel_blog.user.mapper;

import com.wcs.travel_blog.auth.dto.UserRegistrationDTO;
import com.wcs.travel_blog.media.dto.MediaDTO;
import com.wcs.travel_blog.step.dto.SummaryStepDTO;
import com.wcs.travel_blog.travel_diary.dto.SummaryTravelDiaryDTO;
import com.wcs.travel_blog.travel_diary.model.TravelDiary;
import com.wcs.travel_blog.travel_diary.model.TravelStatus;
import com.wcs.travel_blog.user.dto.UpsertUserDTO;
import com.wcs.travel_blog.user.dto.UserDTO;
import com.wcs.travel_blog.user.dto.UserWithDiariesDTO;
import com.wcs.travel_blog.user.model.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Component
public class UserMapper {

    private final PasswordEncoder passwordEncoder;
    public UserMapper(PasswordEncoder passwordEncoder){
        this.passwordEncoder = passwordEncoder;
    }


    public UserDTO converToDto(User user){
        UserDTO userDTO = new UserDTO();
        userDTO.setId(user.getId());
        userDTO.setPseudo(user.getPseudo());
        userDTO.setEmail(user.getEmail());
        userDTO.setAvatar(user.getAvatar());
        userDTO.setBiography(user.getBiography());
        // Récupérer le contenu de l'énum
        if(user.getStatus() != null){
            userDTO.setStatus(user.getStatus().getLabel());
        }

        userDTO.setCreatedAt(user.getCreatedAt());
        userDTO.setUpdatedAt(user.getUpdatedAt());
        userDTO.setRoles(user.getRoles());

        return userDTO;
    }

    public UserWithDiariesDTO converToDtoWithDiaries(User user){
        return converToDtoWithDiaries(user, false);
    }

    public UserWithDiariesDTO converToDtoWithDiaries(User user, boolean includeAllDiaries){
        UserWithDiariesDTO userWithDiariesDTO = new UserWithDiariesDTO();
        userWithDiariesDTO.setId(user.getId());
        userWithDiariesDTO.setPseudo(user.getPseudo());
        userWithDiariesDTO.setEmail(user.getEmail());
        userWithDiariesDTO.setAvatar(user.getAvatar());
        userWithDiariesDTO.setBiography(user.getBiography());
        // Récupérer le contenu de l'énum
        userWithDiariesDTO.setStatus(user.getStatus().getLabel());
        userWithDiariesDTO.setCreatedAt(user.getCreatedAt());
        userWithDiariesDTO.setUpdatedAt(user.getUpdatedAt());
        userWithDiariesDTO.setRoles(user.getRoles());

        List<SummaryTravelDiaryDTO> diariesDTOs = user.getTravel_diaries() == null
                ? List.of()
                : user.getTravel_diaries()
                .stream()
                .filter(diary -> includeAllDiaries || shouldExposeDiary(diary))
                .map(diary -> {
                    SummaryTravelDiaryDTO diaryDTO = new SummaryTravelDiaryDTO();
                    diaryDTO.setId(diary.getId());
                    diaryDTO.setTitle(diary.getTitle());
                    diaryDTO.setDescription(diary.getDescription());
                    diaryDTO.setCreatedAt(diary.getCreatedAt());
                    diaryDTO.setUpdatedAt(diary.getUpdatedAt());
                    diaryDTO.setIsPrivate(diary.getIsPrivate());
                    diaryDTO.setIsPublished(diary.getIsPublished());
                    diaryDTO.setStatus(diary.getStatus());
                    diaryDTO.setCanComment(diary.getCanComment());
                    diaryDTO.setLatitude(diary.getLatitude());
                    diaryDTO.setLongitude(diary.getLongitude());
                    if(diary.getSteps() != null){
                        List<SummaryStepDTO> stepDTOs = diary.getSteps()
                                        .stream()
                                        .map(step -> {
                                            SummaryStepDTO stepDTO = new SummaryStepDTO();
                                            stepDTO.setId(step.getId());
                                            stepDTO.setTitle(step.getTitle());
                                            return stepDTO;
                                        }).toList();
                        diaryDTO.setSteps(stepDTOs);

                    }
                    if (diary.getMedia() != null) {
                        MediaDTO mediaDTO = new MediaDTO();
                        mediaDTO.setId(diary.getMedia().getId());
                        mediaDTO.setFileUrl(diary.getMedia().getFileUrl());
                        mediaDTO.setMediaType(diary.getMedia().getMediaType());
                        mediaDTO.setIsVisible(diary.getMedia().getIsVisible());
                        mediaDTO.setCreatedAt(diary.getMedia().getCreatedAt());
                        mediaDTO.setUpdatedAt(diary.getMedia().getUpdatedAt());
                        diaryDTO.setMedia(mediaDTO);
                    }

                    return diaryDTO;
                }).toList();

        userWithDiariesDTO.setTravelDiaries(diariesDTOs);

        return userWithDiariesDTO;
    }

    private boolean shouldExposeDiary(TravelDiary diary) {
        return Boolean.TRUE.equals(diary.getIsPublished())
                && Boolean.FALSE.equals(diary.getIsPrivate())
                && diary.getStatus() != TravelStatus.DISABLED
                && diary.getSteps() != null
                && !diary.getSteps().isEmpty();
    }

    public User converToEntityOnUpdate(UpsertUserDTO userDTO){

        User user = new User();
        user.setId(userDTO.getId());
        user.setPseudo(userDTO.getPseudo());
        user.setEmail(userDTO.getEmail());
        user.setAvatar(userDTO.getAvatar());
        user.setBiography(userDTO.getBiography());
        // A voir si on garde le status provenant du front ?
        // user.setStatus(UserStatus.valueOf(userDTO.getStatus()));

        return user;
    }

    public User converToEntityOnCreate(UserRegistrationDTO userRegistrationDTO, Set<String> roles){
        User user = new User();
        user.setPseudo(userRegistrationDTO.getPseudo());
        user.setEmail(userRegistrationDTO.getEmail());
        user.setPassword(userRegistrationDTO.getPassword());
        user.setRoles(roles);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        return user;
    }

}
