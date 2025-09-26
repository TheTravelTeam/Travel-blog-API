package com.wcs.travel_blog.search.service;

import com.wcs.travel_blog.search.dto.SearchDiaryDTO;
import com.wcs.travel_blog.search.dto.SearchResponseDTO;
import com.wcs.travel_blog.search.dto.SearchStepDTO;
import com.wcs.travel_blog.step.model.Step;
import com.wcs.travel_blog.step.repository.StepRepository;
import com.wcs.travel_blog.travel_diary.model.TravelDiary;
import com.wcs.travel_blog.travel_diary.repository.TravelDiaryRepository;
import com.wcs.travel_blog.user.model.User;
import com.wcs.travel_blog.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * Agrège les résultats de recherche pour les carnets et les étapes en respectant les règles de visibilité.
 */
@Service
@RequiredArgsConstructor
public class SearchService {

    private static final int EXCERPT_MAX_LENGTH = 160;

    private final TravelDiaryRepository travelDiaryRepository;
    private final StepRepository stepRepository;
    private final UserRepository userRepository;

    /**
     * Exécute une recherche full-text basique sur les carnets et les étapes en fonction du visiteur courant.
     * @param query texte libre saisi dans la barre de recherche (doit être non vide en amont)
     * @return structure combinant les carnets et étapes correspondants
     */
    public SearchResponseDTO search(String query) {
        String normalizedQuery = query.trim();
        Long currentUserId = resolveCurrentUserId();

        List<SearchDiaryDTO> diaries = travelDiaryRepository.searchVisibleDiaries(normalizedQuery, currentUserId)
                .stream()
                .map(this::toDiaryDto)
                .toList();

        List<SearchStepDTO> steps = stepRepository.searchVisibleSteps(normalizedQuery, currentUserId)
                .stream()
                .map(this::toStepDto)
                .toList();

        return SearchResponseDTO.builder()
                .diaries(diaries)
                .steps(steps)
                .build();
    }

    /**
     * Convertit un carnet JPA en DTO de résultat de recherche.
     * @param travelDiary entité JPA obtenue depuis le repository
     * @return DTO avec métadonnées principales et cover éventuelle
     */
    private SearchDiaryDTO toDiaryDto(TravelDiary travelDiary) {
        String coverUrl = null;
        if (travelDiary.getMedia() != null && Boolean.TRUE.equals(travelDiary.getMedia().getIsVisible())) {
            coverUrl = travelDiary.getMedia().getFileUrl();
        }

        return SearchDiaryDTO.builder()
                .id(travelDiary.getId())
                .title(travelDiary.getTitle())
                .description(travelDiary.getDescription())
                .coverUrl(coverUrl)
                .build();
    }

    /**
     * Convertit une étape JPA en DTO de résultat de recherche.
     * @param step entité JPA (avec carnet associé)
     * @return DTO contenant titre, excerpt et informations de rattachement au carnet
     */
    private SearchStepDTO toStepDto(Step step) {
        TravelDiary diary = step.getTravelDiary();
        return SearchStepDTO.builder()
                .id(step.getId())
                .title(step.getTitle())
                .diaryId(diary != null ? diary.getId() : null)
                .diaryTitle(diary != null ? diary.getTitle() : null)
                .excerpt(buildExcerpt(step.getDescription()))
                .build();
    }

    /**
     * Génère un extrait utilisateur à partir d'une description potentiellement longue.
     * @param description texte libre de l'étape
     * @return extrait tronqué à {@value EXCERPT_MAX_LENGTH} caractères, ou chaîne vide si description absente
     */
    private String buildExcerpt(String description) {
        if (!StringUtils.hasText(description)) {
            return "";
        }

        String trimmed = description.trim();
        if (trimmed.length() <= EXCERPT_MAX_LENGTH) {
            return trimmed;
        }
        return trimmed.substring(0, EXCERPT_MAX_LENGTH).trim() + "...";
    }

    /**
     * Récupère l'identifiant de l'utilisateur courant à partir du SecurityContext.
     * @return identifiant si l'utilisateur est authentifié, {@code null} sinon
     */
    private Long resolveCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || authentication instanceof AnonymousAuthenticationToken) {
            return null;
        }

        Object principal = authentication.getPrincipal();
        if (principal instanceof User user) {
            return user.getId();
        }

        if (principal instanceof UserDetails userDetails) {
            return userRepository.findByEmail(userDetails.getUsername())
                    .map(User::getId)
                    .orElse(null);
        }

        if (principal instanceof String username && StringUtils.hasText(username) && !"anonymousUser".equals(username)) {
            return userRepository.findByEmail(username)
                    .map(User::getId)
                    .orElse(null);
        }

        return null;
    }
}
