package com.wcs.travel_blog.user.service;

import com.wcs.travel_blog.exception.ResourceNotFoundException;
import com.wcs.travel_blog.step.model.Step;
import com.wcs.travel_blog.travel_diary.model.TravelDiary;
import com.wcs.travel_blog.travel_diary.model.TravelStatus;
import com.wcs.travel_blog.user.dto.UpsertUserDTO;
import com.wcs.travel_blog.user.dto.UserDTO;
import com.wcs.travel_blog.user.dto.UserWithDiariesDTO;
import com.wcs.travel_blog.user.mapper.UserMapper;
import com.wcs.travel_blog.user.model.User;
import com.wcs.travel_blog.user.model.UserStatus;
import com.wcs.travel_blog.user.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, UserMapper userMapper, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
    }

    public List<UserDTO> getAllUsers(){
        List<User> users = userRepository.findAll();
        return users.stream().map(userMapper::converToDto).collect(Collectors.toList());
    }

    /**
     * Fetches a user profile with diary summaries. Admins or the profile owner get the full diary list, while other
     * visitors receive only public/published diaries (filter handled downstream by the mapper).
     */
    public UserWithDiariesDTO getUserById(Long userId, Long currentUserId, boolean isAdmin){
        User user = userRepository.findById(userId).orElseThrow(()-> new ResourceNotFoundException("user non trouvé avec l'id : " + userId));
        boolean isOwner = currentUserId != null && currentUserId.equals(userId);

        return userMapper.converToDtoWithDiaries(user, isOwner || isAdmin);
    }

    public UserDTO getUserByEmail(String userEmail){
        User user = userRepository.findByEmail(userEmail).orElseThrow(()-> new ResourceNotFoundException("user non trouvé avec l'email : " + userEmail));
        return userMapper.converToDto(user);
    }

    public UserWithDiariesDTO getUserWithDiariesByEmail(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("user non trouvé avec l'email : " + userEmail));
        return userMapper.converToDtoWithDiaries(user, true);
    }

        public UserWithDiariesDTO getUserByPseudo(String username){
        User user = userRepository.findByPseudo(username).orElseThrow(()-> new ResourceNotFoundException("user non trouvé avec l'username : " + username));
        return userMapper.converToDtoWithDiaries(user);
    }

    @Transactional
    public UserDTO updateUser(Long userId, UpsertUserDTO upsertUserDTO){
        User existingUser = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("user non trouvé avec l'id : " + userId));

        UserStatus previousStatus = existingUser.getStatus();

        existingUser.setUpdatedAt(LocalDateTime.now());
        existingUser.setPseudo(upsertUserDTO.getPseudo());
        existingUser.setEmail(upsertUserDTO.getEmail());
        if(upsertUserDTO.getBiography() != null  && !upsertUserDTO.getBiography().equals(existingUser.getBiography())){
            existingUser.setBiography(upsertUserDTO.getBiography());
        }
        if(upsertUserDTO.getAvatar() != null && !upsertUserDTO.getAvatar().equals(existingUser.getAvatar())){
            existingUser.setAvatar(upsertUserDTO.getAvatar());
        }

        UserStatus requestedStatus = upsertUserDTO.getStatus();
        if (requestedStatus != null) {
            existingUser.setStatus(requestedStatus);
        }

        if (shouldDisableContent(previousStatus, requestedStatus)) {
            disableUserContent(existingUser);
        } else if (shouldRestoreContent(previousStatus, requestedStatus)) {
            restoreUserContent(existingUser);
        }
        if(upsertUserDTO.getPassword() != null && !upsertUserDTO.getPassword().isBlank()){
            existingUser.setPassword(passwordEncoder.encode(upsertUserDTO.getPassword()));
        }

        return userMapper.converToDto(userRepository.save(existingUser));
    }


    public void deleteUserById(Long userId){
        User user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("Aucun utilisateur trouvé avec l'id : " + userId));
        userRepository.delete(user);
    }

    public UserDTO updateUserRoles(Long userId, boolean isAdmin) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("user non trouvé avec l'id : " + userId));

        Set<String> roles = new HashSet<>();
        roles.add("ROLE_USER");
        if (isAdmin) {
            roles.add("ROLE_ADMIN");
        }

        user.setRoles(roles);
        user.setUpdatedAt(LocalDateTime.now());

        User savedUser = userRepository.save(user);
        return userMapper.converToDto(savedUser);
    }

    private boolean shouldDisableContent(UserStatus previousStatus, UserStatus requestedStatus) {
        return requestedStatus != null
                && requestedStatus == UserStatus.BLOCKED
                && previousStatus != UserStatus.BLOCKED;
    }

    private boolean shouldRestoreContent(UserStatus previousStatus, UserStatus requestedStatus) {
        return requestedStatus != null
                && requestedStatus == UserStatus.ACTIVE
                && previousStatus == UserStatus.BLOCKED;
    }

    private void disableUserContent(User user) {
        List<TravelDiary> diaries = user.getTravel_diaries();
        if (diaries == null || diaries.isEmpty()) {
            return;
        }

        LocalDateTime now = LocalDateTime.now();
        diaries.stream()
                .filter(diary -> diary != null)
                .forEach(diary -> {
                    if (diary.getStatus() != TravelStatus.DISABLED) {
                        diary.setStatus(TravelStatus.DISABLED);
                        diary.setUpdatedAt(now);
                    }
                    if (diary.getSteps() == null) {
                        return;
                    }
                    diary.getSteps().stream()
                            .filter(step -> step != null)
                            .forEach(step -> {
                                if (step.getStatus() != TravelStatus.DISABLED) {
                                    step.setStatus(TravelStatus.DISABLED);
                                    step.setUpdatedAt(now);
                                }
                            });
                });
    }

    private void restoreUserContent(User user) {
        List<TravelDiary> diaries = user.getTravel_diaries();
        if (diaries == null || diaries.isEmpty()) {
            return;
        }

        LocalDateTime now = LocalDateTime.now();
        diaries.stream()
                .filter(diary -> diary != null)
                .forEach(diary -> {
                    if (diary.getStatus() == TravelStatus.DISABLED) {
                        diary.setStatus(resolveDiaryStatus(diary));
                        diary.setUpdatedAt(now);
                    }
                    if (diary.getSteps() == null) {
                        return;
                    }
                    diary.getSteps().stream()
                            .filter(step -> step != null)
                            .forEach(step -> {
                                if (step.getStatus() == TravelStatus.DISABLED) {
                                    step.setStatus(resolveStepStatus(step));
                                    step.setUpdatedAt(now);
                                }
                            });
                });
    }

    private TravelStatus resolveDiaryStatus(TravelDiary diary) {
        return diary.getEndDate() != null ? TravelStatus.COMPLETED : TravelStatus.IN_PROGRESS;
    }

    private TravelStatus resolveStepStatus(Step step) {
        return step.getStartDate() != null && step.getEndDate() != null
                ? TravelStatus.COMPLETED
                : TravelStatus.IN_PROGRESS;
    }
}
