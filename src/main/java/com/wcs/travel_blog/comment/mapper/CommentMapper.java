package com.wcs.travel_blog.comment.mapper;


import com.wcs.travel_blog.comment.dto.CommentDTO;
import com.wcs.travel_blog.comment.model.Comment;
import com.wcs.travel_blog.user.mapper.UserMapper;
import org.springframework.stereotype.Component;

@Component
public class CommentMapper {
    private final UserMapper userMapper;

    public CommentMapper(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    public CommentDTO converToDto(Comment comment){
        CommentDTO commentDTO = new CommentDTO();
        commentDTO.setId(comment.getId());
        commentDTO.setContent(comment.getContent());
        commentDTO.setStatus(comment.getStatus());
        commentDTO.setUpdatedAt(comment.getUpdatedAt());
        commentDTO.setCreatedAt(comment.getCreatedAt());
        commentDTO.setUser(userMapper.converToDto(comment.getUser()));
        return commentDTO;

    }

//    public UserWithDiariesDTO converToDtoWithDiaries(User user){
//        UserWithDiariesDTO UserWithDiariesDTO = new UserWithDiariesDTO();
//        UserWithDiariesDTO.setId(user.getId());
//        UserWithDiariesDTO.setUsername(user.getUsername());
//        UserWithDiariesDTO.setEmail(user.getEmail());
//        UserWithDiariesDTO.setAvatar(user.getAvatar());
//        UserWithDiariesDTO.setBiography(user.getBiography());
//        // Récupérer le contenu de l'énum
//        UserWithDiariesDTO.setStatus(user.getStatus().getLabel());
//        UserWithDiariesDTO.setCreatedAt(user.getCreatedAt());
//        UserWithDiariesDTO.setUpdatedAt(user.getUpdatedAt());
//        UserWithDiariesDTO.setRoles(user.getRoles());
//
//        List<SummaryTravelDiaryDTO> diariesDTOs = user.getTravel_diaries()
//                .stream()
//                .map(diary -> {
//                    SummaryTravelDiaryDTO diaryDTO = new SummaryTravelDiaryDTO();
//                    diaryDTO.setId(diary.getId());
//                    diaryDTO.setTitle(diary.getTitle());
//                    diaryDTO.setDescription(diary.getDescription());
//                    diaryDTO.setCreatedAt(diary.getCreatedAt());
//                    diaryDTO.setUpdatedAt(diary.getUpdatedAt());
//                    diaryDTO.setIsPrivate(diary.getIsPrivate());
//                    diaryDTO.setIsPublished(diary.getIsPublished());
//                    diaryDTO.setStatus(diary.getStatus());
//                    diaryDTO.setCanComment(diary.getCanComment());
//                    diaryDTO.setLatitude(diary.getLatitude());
//                    diaryDTO.setLongitude(diary.getLongitude());
//                    if(diary.getSteps() != null){
//                        List<SummaryStepDTO> stepDTOs = diary.getSteps()
//                                .stream()
//                                .map(step -> {
//                                    SummaryStepDTO stepDTO = new SummaryStepDTO();
//                                    stepDTO.setId(step.getId());
//                                    stepDTO.setTitle(step.getTitle());
//                                    return stepDTO;
//                                }).toList();
//                        diaryDTO.setSteps(stepDTOs);
//
//                    }
//                    if (diary.getMedia() != null) {
//                        MediaDTO mediaDTO = new MediaDTO();
//                        mediaDTO.setId(diary.getMedia().getId());
//                        mediaDTO.setFileUrl(diary.getMedia().getFileUrl());
//                        mediaDTO.setMediaType(diary.getMedia().getMediaType().name());
//                        mediaDTO.setIsVisible(diary.getMedia().getIsVisible());
//                        mediaDTO.setCreatedAt(diary.getMedia().getCreatedAt());
//                        mediaDTO.setUpdatedAt(diary.getMedia().getUpdatedAt());
//                        diaryDTO.setMedia(mediaDTO);
//                    }
//
//                    return diaryDTO;
//                }).toList();
//
//        UserWithDiariesDTO.setTravelDiaries(diariesDTOs);
//
//        return UserWithDiariesDTO;
//    }
//
//    public User converToEntity(UserRegistrationDTO userRegistrationDTO, Set<String> roles){
//        User user = new User();
//        user.setUsername(userRegistrationDTO.getUsername());
//        user.setEmail(userRegistrationDTO.getEmail());
//        user.setPassword(userRegistrationDTO.getPassword());
//        user.setRoles(roles);
//        user.setCreatedAt(LocalDateTime.now());
//        user.setUpdatedAt(LocalDateTime.now());
//        return user;
//    }
}
