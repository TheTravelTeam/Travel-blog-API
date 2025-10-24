package com.wcs.travel_blog.travel_diary.service;

import com.wcs.travel_blog.media.repository.MediaRepository;
import com.wcs.travel_blog.step.repository.StepRepository;
import com.wcs.travel_blog.travel_diary.dto.CreateTravelDiaryDTO;
import com.wcs.travel_blog.travel_diary.dto.TravelDiaryDTO;
import com.wcs.travel_blog.travel_diary.dto.UpdateTravelDiaryDTO;
import com.wcs.travel_blog.travel_diary.mapper.TravelDiaryMapper;
import com.wcs.travel_blog.travel_diary.model.TravelDiary;
import com.wcs.travel_blog.travel_diary.repository.TravelDiaryRepository;
import com.wcs.travel_blog.travel_diary.model.TravelStatus;
import com.wcs.travel_blog.user.repository.UserRepository;
import com.wcs.travel_blog.util.HtmlSanitizerService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TravelDiaryServiceTest {

    @Mock
    private TravelDiaryRepository travelDiaryRepository;

    @Mock
    private TravelDiaryMapper travelDiaryMapper;

    @Mock
    private UserRepository userRepository;

    @Mock
    private MediaRepository mediaRepository;

    @Mock
    private StepRepository stepRepository;

    @InjectMocks
    private TravelDiaryService travelDiaryService;

    @Mock(strictness = Mock.Strictness.LENIENT)
    private HtmlSanitizerService htmlSanitizerService;

    @Test
    @DisplayName("createTravelDiary force isPublished à false lorsqu'il n'y a pas d'étape")
    void shouldForceIsPublishedFalseWhenCreatingDiaryWithoutSteps() {
        CreateTravelDiaryDTO request = new CreateTravelDiaryDTO();
        request.setIsPublished(Boolean.TRUE);

        TravelDiary mappedEntity = new TravelDiary();
        mappedEntity.setIsPublished(Boolean.TRUE);
        mappedEntity.setCreatedAt(LocalDateTime.now());
        mappedEntity.setUpdatedAt(LocalDateTime.now());
        mappedEntity.setIsPrivate(null);

        when(travelDiaryMapper.toEntity(request)).thenReturn(mappedEntity);
        when(travelDiaryRepository.save(any(TravelDiary.class))).thenReturn(mappedEntity);
        when(travelDiaryMapper.toDto(mappedEntity)).thenReturn(new TravelDiaryDTO());

        travelDiaryService.createTravelDiary(request);

        verify(travelDiaryRepository).save(mappedEntity);
        assertThat(mappedEntity.getIsPublished()).isFalse();
        assertThat(mappedEntity.getIsPrivate()).isFalse();
        verify(stepRepository, never()).findAllById(any());
    }

    @Test
    @DisplayName("createTravelDiary conserve isPublished lorsque des étapes sont associées")
    void shouldKeepIsPublishedTrueWhenStepsAreProvided() {
        CreateTravelDiaryDTO request = new CreateTravelDiaryDTO();
        request.setIsPublished(Boolean.TRUE);
        request.setSteps(List.of(1L));

        TravelDiary mappedEntity = new TravelDiary();
        mappedEntity.setIsPublished(Boolean.TRUE);
        mappedEntity.setCreatedAt(LocalDateTime.now());
        mappedEntity.setUpdatedAt(LocalDateTime.now());
        mappedEntity.setIsPrivate(Boolean.TRUE);

        com.wcs.travel_blog.step.model.Step step = new com.wcs.travel_blog.step.model.Step();
        step.setId(1L);
        step.setTitle("Step");
        step.setCity("City");
        step.setCountry("Country");
        step.setContinent("Continent");
        step.setTravelDiary(mappedEntity);

        when(travelDiaryMapper.toEntity(request)).thenReturn(mappedEntity);
        when(stepRepository.findAllById(request.getSteps())).thenReturn(List.of(step));
        when(travelDiaryRepository.save(any(TravelDiary.class))).thenReturn(mappedEntity);
        when(travelDiaryMapper.toDto(mappedEntity)).thenReturn(new TravelDiaryDTO());

        travelDiaryService.createTravelDiary(request);

        verify(travelDiaryRepository).save(mappedEntity);
        assertThat(mappedEntity.getIsPublished()).isTrue();
        assertThat(mappedEntity.getSteps()).hasSize(1);
        assertThat(mappedEntity.getIsPrivate()).isTrue();
    }

    @Test
    @DisplayName("createTravelDiary passe toutes les étapes en disabled quand le carnet est désactivé")
    void shouldDisableStepsWhenCreatingDisabledDiary() {
        CreateTravelDiaryDTO request = new CreateTravelDiaryDTO();
        request.setIsPublished(Boolean.TRUE);
        request.setSteps(List.of(1L));

        TravelDiary mappedEntity = new TravelDiary();
        mappedEntity.setStatus(TravelStatus.DISABLED);
        mappedEntity.setIsPublished(Boolean.TRUE);
        mappedEntity.setCreatedAt(LocalDateTime.now());
        mappedEntity.setUpdatedAt(LocalDateTime.now());

        com.wcs.travel_blog.step.model.Step step = new com.wcs.travel_blog.step.model.Step();
        step.setId(1L);
        step.setTitle("Step");
        step.setCity("City");
        step.setCountry("Country");
        step.setContinent("Continent");
        step.setStatus(TravelStatus.COMPLETED);
        step.setTravelDiary(mappedEntity);

        when(travelDiaryMapper.toEntity(request)).thenReturn(mappedEntity);
        when(stepRepository.findAllById(request.getSteps())).thenReturn(List.of(step));
        when(travelDiaryRepository.save(mappedEntity)).thenReturn(mappedEntity);
        when(travelDiaryMapper.toDto(mappedEntity)).thenReturn(new TravelDiaryDTO());

        travelDiaryService.createTravelDiary(request);

        assertThat(step.getStatus()).isEqualTo(TravelStatus.DISABLED);
    }

    @Test
    @DisplayName("updateTravelDiary remet isPublished à false lorsque toutes les étapes sont supprimées")
    void shouldForceIsPublishedFalseWhenStepsAreClearedOnUpdate() {
        UpdateTravelDiaryDTO request = new UpdateTravelDiaryDTO();
        request.setSteps(List.of());
        request.setIsPublished(Boolean.TRUE);

        TravelDiary existing = new TravelDiary();
        existing.setId(1L);
        existing.setIsPublished(Boolean.TRUE);
        existing.setCreatedAt(LocalDateTime.now());
        existing.setUpdatedAt(LocalDateTime.now());
        existing.setIsPrivate(null);
        com.wcs.travel_blog.step.model.Step existingStep = new com.wcs.travel_blog.step.model.Step();
        existingStep.setTitle("Existing step");
        existingStep.setCity("City");
        existingStep.setCountry("Country");
        existingStep.setContinent("Continent");
        existing.setSteps(new ArrayList<>(List.of(existingStep)));

        when(travelDiaryRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(travelDiaryRepository.save(existing)).thenReturn(existing);
        when(travelDiaryMapper.toDto(existing)).thenReturn(new TravelDiaryDTO());

        travelDiaryService.updateTravelDiary(1L, request);

        verify(travelDiaryRepository).save(existing);
        assertThat(existing.getIsPublished()).isFalse();
        assertThat(existing.getSteps()).isEmpty();
        assertThat(existing.getIsPrivate()).isFalse();
    }

    @Test
    @DisplayName("updateTravelDiary passe toutes les étapes en disabled lorsque le carnet est désactivé")
    void shouldDisableStepsWhenDiaryStatusBecomesDisabled() {
        UpdateTravelDiaryDTO request = new UpdateTravelDiaryDTO();
        request.setStatus(TravelStatus.DISABLED);

        TravelDiary existing = new TravelDiary();
        existing.setId(1L);
        existing.setStatus(TravelStatus.IN_PROGRESS);
        existing.setCreatedAt(LocalDateTime.now());
        existing.setUpdatedAt(LocalDateTime.now());

        com.wcs.travel_blog.step.model.Step step = new com.wcs.travel_blog.step.model.Step();
        step.setTitle("Existing step");
        step.setCity("City");
        step.setCountry("Country");
        step.setContinent("Continent");
        step.setStatus(TravelStatus.COMPLETED);
        existing.setSteps(new ArrayList<>(List.of(step)));

        when(travelDiaryRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(travelDiaryRepository.save(existing)).thenReturn(existing);
        when(travelDiaryMapper.toDto(existing)).thenReturn(new TravelDiaryDTO());

        travelDiaryService.updateTravelDiary(1L, request);

        assertThat(existing.getStatus()).isEqualTo(TravelStatus.DISABLED);
        assertThat(step.getStatus()).isEqualTo(TravelStatus.DISABLED);
    }

    @Test
    @DisplayName("createTravelDiary conserve isPrivate lorsqu'il est fourni")
    void shouldKeepIsPrivateWhenProvided() {
        CreateTravelDiaryDTO request = new CreateTravelDiaryDTO();
        request.setIsPrivate(Boolean.TRUE);

        TravelDiary mappedEntity = new TravelDiary();
        mappedEntity.setIsPrivate(Boolean.TRUE);
        mappedEntity.setIsPublished(Boolean.FALSE);
        mappedEntity.setCreatedAt(LocalDateTime.now());
        mappedEntity.setUpdatedAt(LocalDateTime.now());

        when(travelDiaryMapper.toEntity(request)).thenReturn(mappedEntity);
        when(travelDiaryRepository.save(any(TravelDiary.class))).thenReturn(mappedEntity);
        when(travelDiaryMapper.toDto(mappedEntity)).thenReturn(new TravelDiaryDTO());

        travelDiaryService.createTravelDiary(request);

        verify(travelDiaryRepository).save(mappedEntity);
        assertThat(mappedEntity.getIsPrivate()).isTrue();
    }

    @Test
    @DisplayName("getTravelDiariesForUser retourne tous les carnets pour le propriétaire connecté")
    void shouldReturnAllDiariesForOwner() {
        TravelDiary diary1 = new TravelDiary();
        diary1.setId(1L);
        TravelDiary diary2 = new TravelDiary();
        diary2.setId(2L);

        TravelDiaryDTO dto1 = new TravelDiaryDTO();
        dto1.setId(1L);
        TravelDiaryDTO dto2 = new TravelDiaryDTO();
        dto2.setId(2L);

        when(travelDiaryRepository.findAllByUserIdOrderByUpdatedAtDesc(42L)).thenReturn(List.of(diary1, diary2));
        when(travelDiaryMapper.toDto(diary1)).thenReturn(dto1);
        when(travelDiaryMapper.toDto(diary2)).thenReturn(dto2);

        List<TravelDiaryDTO> result = travelDiaryService.getTravelDiariesForUser(42L, 42L, false);

        assertThat(result).containsExactly(dto1, dto2);
        verify(travelDiaryRepository).findAllByUserIdOrderByUpdatedAtDesc(42L);
        verify(travelDiaryRepository, never()).findPublishedPublicByUserId(42L);
    }

    @Test
    @DisplayName("getTravelDiariesForUser retourne uniquement les carnets publics d'un autre utilisateur")
    void shouldReturnOnlyPublicDiariesForOtherUser() {
        TravelDiary diary = new TravelDiary();
        diary.setId(1L);

        TravelDiaryDTO dto = new TravelDiaryDTO();
        dto.setId(1L);

        when(travelDiaryRepository.findPublishedPublicByUserId(42L)).thenReturn(List.of(diary));
        when(travelDiaryMapper.toDto(diary)).thenReturn(dto);

        List<TravelDiaryDTO> result = travelDiaryService.getTravelDiariesForUser(42L, 100L, false);

        assertThat(result).containsExactly(dto);
        verify(travelDiaryRepository).findPublishedPublicByUserId(42L);
        verify(travelDiaryRepository, never()).findAllByUserIdOrderByUpdatedAtDesc(42L);
    }

    @Test
    @DisplayName("getTravelDiariesForUser retourne tous les carnets pour un admin")
    void shouldReturnAllDiariesForAdmin() {
        TravelDiary diary1 = new TravelDiary();
        diary1.setId(1L);
        TravelDiary diary2 = new TravelDiary();
        diary2.setId(2L);

        TravelDiaryDTO dto1 = new TravelDiaryDTO();
        dto1.setId(1L);
        TravelDiaryDTO dto2 = new TravelDiaryDTO();
        dto2.setId(2L);

        when(travelDiaryRepository.findAllByUserIdOrderByUpdatedAtDesc(42L)).thenReturn(List.of(diary1, diary2));
        when(travelDiaryMapper.toDto(diary1)).thenReturn(dto1);
        when(travelDiaryMapper.toDto(diary2)).thenReturn(dto2);

        List<TravelDiaryDTO> result = travelDiaryService.getTravelDiariesForUser(42L, 100L, true);

        assertThat(result).containsExactly(dto1, dto2);
        verify(travelDiaryRepository).findAllByUserIdOrderByUpdatedAtDesc(42L);
        verify(travelDiaryRepository, never()).findPublishedPublicByUserId(42L);
    }
}
