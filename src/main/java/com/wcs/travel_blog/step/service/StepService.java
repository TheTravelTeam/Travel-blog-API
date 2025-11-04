package com.wcs.travel_blog.step.service;

import com.wcs.travel_blog.exception.ResourceNotFoundException;
import com.wcs.travel_blog.step.dto.StepRequestDTO;
import com.wcs.travel_blog.step.dto.StepResponseDTO;
import com.wcs.travel_blog.step.mapper.StepMapper;
import com.wcs.travel_blog.step.model.Step;
import com.wcs.travel_blog.step.model.StepLike;
import com.wcs.travel_blog.step.repository.StepLikeRepository;
import com.wcs.travel_blog.step.repository.StepRepository;
import com.wcs.travel_blog.theme.model.Theme;
import com.wcs.travel_blog.theme.repository.ThemeRepository;
import com.wcs.travel_blog.util.CurrentUserProvider;
import com.wcs.travel_blog.user.model.User;
import com.wcs.travel_blog.user.repository.UserRepository;
import com.wcs.travel_blog.exception.ForbiddenOperationException;
import com.wcs.travel_blog.travel_diary.model.TravelDiary;
import com.wcs.travel_blog.travel_diary.model.TravelStatus;
import com.wcs.travel_blog.travel_diary.repository.TravelDiaryRepository;
import com.wcs.travel_blog.util.HtmlSanitizerService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class StepService {
    private final StepRepository stepRepository;
    private final StepMapper stepMapper;
    private final ThemeRepository themeRepository;
    private final StepLikeRepository stepLikeRepository;
    private final CurrentUserProvider currentUserProvider;
    private final HtmlSanitizerService htmlSanitizerService;
    private final TravelDiaryRepository travelDiaryRepository;
    private final UserRepository userRepository;

    public StepService(StepRepository stepRepository,
                       StepMapper stepMapper,
                       ThemeRepository themeRepository,
                       StepLikeRepository stepLikeRepository,
                       CurrentUserProvider currentUserProvider,
                       HtmlSanitizerService htmlSanitizerService,
                       TravelDiaryRepository travelDiaryRepository,
                       UserRepository userRepository) {
        this.stepRepository = stepRepository;
        this.stepMapper = stepMapper;
        this.themeRepository = themeRepository;
        this.stepLikeRepository = stepLikeRepository;
        this.currentUserProvider = currentUserProvider;
        this.htmlSanitizerService = htmlSanitizerService;
        this.travelDiaryRepository = travelDiaryRepository;
        this.userRepository = userRepository;
    }

    public List<StepResponseDTO> getAllSteps() {
        List<Step> steps = stepRepository.findAll();
        return steps.stream()
                .map(stepMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    public StepResponseDTO getStepById(Long stepId) {
        Step step = stepRepository.findById(stepId)
                .orElseThrow(() -> new ResourceNotFoundException("Step not found with id: " + stepId));
        return stepMapper.toResponseDto(step);
    }

    public StepResponseDTO createStep(StepRequestDTO stepDto, Long currentUserId) {
        Step step = stepMapper.toEntity(stepDto);
        if (stepDto.getTravelDiaryId() == null) {
            throw new ResourceNotFoundException("L'identifiant du carnet de voyage est requis");
        }
        TravelDiary diary = travelDiaryRepository.findById(stepDto.getTravelDiaryId())
                .orElseThrow(() -> new ResourceNotFoundException("Carnet de voyage introuvable avec l'id: " + stepDto.getTravelDiaryId()));

        boolean isOwner = diary.getUser() != null && diary.getUser().getId() != null && diary.getUser().getId().equals(currentUserId);
        if (!isOwner) {
            throw new ForbiddenOperationException("Non autorisé à ajouter une étape à ce carnet de voyage.");
        }
        step.setTravelDiary(diary);
        // nettoyage des champs sensibles
        step.setTitle(htmlSanitizerService.sanitize(step.getTitle()));
        step.setDescription(htmlSanitizerService.sanitize(step.getDescription()));
        step.setLikesCount(0L);
        step.setCreatedAt(LocalDateTime.now());
        step.setUpdatedAt(LocalDateTime.now());
        step.setThemes(resolveThemes(stepDto.getThemeIds()));
        if (step.getStatus() == null) {
            step.setStatus(resolveStepStatus(step));
        }
        Step savedStep = stepRepository.save(step);
        return stepMapper.toResponseDto(savedStep);
    }

    public StepResponseDTO updateStep(Long stepId, StepRequestDTO stepDto, Long currentUserId) {
        Step existingStep = stepRepository.findById(stepId)
                .orElseThrow(() -> new ResourceNotFoundException("Step not found with id: " + stepId));

        TravelDiary diary = existingStep.getTravelDiary();
        boolean isAdmin = isAdmin(currentUserId);
        boolean isOwner = diary != null && diary.getUser() != null && diary.getUser().getId() != null && diary.getUser().getId().equals(currentUserId);
        if (!isAdmin && !isOwner) {
            throw new ForbiddenOperationException("Non autorisé à modifier cette étape.");
        }

        existingStep.setTitle(htmlSanitizerService.sanitize(stepDto.getTitle()));
        existingStep.setDescription(htmlSanitizerService.sanitize(stepDto.getDescription()));
        existingStep.setStartDate(stepDto.getStartDate());
        existingStep.setEndDate(stepDto.getEndDate());
        if (stepDto.getStatus() != null) {
            existingStep.setStatus(stepDto.getStatus());
        } else if (existingStep.getStatus() != TravelStatus.DISABLED) {
            existingStep.setStatus(resolveStepStatus(existingStep));
        }
        existingStep.setLatitude(stepDto.getLatitude());
        existingStep.setLongitude(stepDto.getLongitude());
        existingStep.setCity(stepDto.getCity());
        existingStep.setCountry(stepDto.getCountry());
        existingStep.setContinent(stepDto.getContinent());
        existingStep.setUpdatedAt(LocalDateTime.now());
        existingStep.setThemes(resolveThemes(stepDto.getThemeIds()));

        Step updatedStep = stepRepository.save(existingStep);
        return stepMapper.toResponseDto(updatedStep);
    }

    @Transactional
    public StepResponseDTO updateLikes(Long stepId, boolean increment, Long currentUserId) {
        Step step = stepRepository.findById(stepId)
                .orElseThrow(() -> new ResourceNotFoundException("Step not found with id: " + stepId));

        Optional<StepLike> existingLike = stepLikeRepository.findByStepIdAndUserId(stepId, currentUserId);

        if (increment) {
            if (existingLike.isEmpty()) {
                StepLike newLike = new StepLike();
                newLike.setStep(step);
                User u = new User();
                u.setId(currentUserId);
                newLike.setUser(u);
                stepLikeRepository.save(newLike);
            }
        } else {
            existingLike.ifPresent(stepLikeRepository::delete);
        }

        long updatedLikes = stepLikeRepository.countByStepId(stepId);
        step.setLikesCount(updatedLikes);
        step.setUpdatedAt(LocalDateTime.now());

        Step updatedStep = stepRepository.save(step);
        StepResponseDTO response = stepMapper.toResponseDto(updatedStep);
        response.setViewerHasLiked(increment);
        return response;
    }

    public void deleteStep(Long stepId, Long currentUserId) {
        Step existingStep = stepRepository.findById(stepId)
                .orElseThrow(() -> new ResourceNotFoundException("Step not found with id: " + stepId));

        TravelDiary diary = existingStep.getTravelDiary();
        boolean isAdmin = isAdmin(currentUserId);
        boolean isOwner = diary != null && diary.getUser() != null && diary.getUser().getId() != null && diary.getUser().getId().equals(currentUserId);
        if (!isAdmin && !isOwner) {
            throw new ForbiddenOperationException("Non autorisé à supprimer cette étape.");
        }
        stepRepository.delete(existingStep);
    }

    private boolean isAdmin(Long userId) {
        if (userId == null) {
            return false;
        }
        return userRepository.findById(userId)
                .map(User::getRoles)
                .map(roles -> roles.contains("ROLE_ADMIN"))
                .orElse(false);
    }

    private List<Theme> resolveThemes(List<Long> themeIds) {
        if (themeIds == null || themeIds.isEmpty()) {
            return new ArrayList<>();
        }

        List<Long> distinctThemeIds = themeIds.stream()
                .filter(Objects::nonNull)
                .collect(Collectors.collectingAndThen(
                        Collectors.toCollection(LinkedHashSet::new),
                        ArrayList::new
                ));

        if (distinctThemeIds.isEmpty()) {
            return new ArrayList<>();
        }

        List<Theme> themes = themeRepository.findAllById(distinctThemeIds);
        if (themes.size() != distinctThemeIds.size()) {
            throw new ResourceNotFoundException("Un ou plusieurs thèmes sont introuvables");
        }
        return new ArrayList<>(themes);
    }

    private TravelStatus resolveStepStatus(Step step) {
        return step.getStartDate() != null && step.getEndDate() != null
                ? TravelStatus.COMPLETED
                : TravelStatus.IN_PROGRESS;
    }

}
