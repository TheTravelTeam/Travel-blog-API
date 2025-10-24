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
import com.wcs.travel_blog.user.model.User;
import com.wcs.travel_blog.user.repository.UserRepository;
import com.wcs.travel_blog.travel_diary.model.TravelDiary;
import com.wcs.travel_blog.travel_diary.repository.TravelDiaryRepository;
import com.wcs.travel_blog.util.CurrentUserProvider;
import com.wcs.travel_blog.util.HtmlSanitizerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
class StepServiceTest {

    @Mock
    private StepRepository stepRepository;

    @Mock
    private StepMapper stepMapper;

    @Mock
    private ThemeRepository themeRepository;

    @Mock
    private StepLikeRepository stepLikeRepository;

    @Mock
    private CurrentUserProvider currentUserProvider;

    @Mock
    private TravelDiaryRepository travelDiaryRepository;

    @Mock
    private UserRepository userRepository;

    @Mock(strictness = Mock.Strictness.LENIENT)
    private HtmlSanitizerService htmlSanitizerService;

    @InjectMocks
    private StepService stepService;

    @Test
    void createStep_shouldAssignThemesFromIds() {
        StepRequestDTO input = new StepRequestDTO();
        input.setThemeIds(List.of(1L, 2L));
        input.setTravelDiaryId(100L);

        Step mappedEntity = new Step();
        mappedEntity.setLikesCount(5L);
        // owner diary
        TravelDiary diary = new TravelDiary();
        User owner = user(999L);
        diary.setUser(owner);
        Theme theme1 = theme(1L);
        Theme theme2 = theme(2L);
        StepResponseDTO mappedDto = new StepResponseDTO();

        when(stepMapper.toEntity(input)).thenReturn(mappedEntity);
        when(themeRepository.findAllById(List.of(1L, 2L))).thenReturn(List.of(theme1, theme2));
        when(travelDiaryRepository.findById(100L)).thenReturn(Optional.of(diary));
        when(stepRepository.save(any(Step.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(stepMapper.toResponseDto(any(Step.class))).thenReturn(mappedDto);

        StepResponseDTO result = stepService.createStep(input, 999L);

        ArgumentCaptor<Step> captor = ArgumentCaptor.forClass(Step.class);
        verify(stepRepository).save(captor.capture());
        Step saved = captor.getValue();

        assertThat(saved.getThemes()).containsExactly(theme1, theme2);
        assertThat(saved.getLikesCount()).isZero();
        assertThat(result).isSameAs(mappedDto);
    }

    @Test
    void createStep_shouldThrowWhenThemeMissing() {
        StepRequestDTO input = new StepRequestDTO();
        input.setThemeIds(List.of(1L, 2L));
        input.setTravelDiaryId(200L);

        when(stepMapper.toEntity(input)).thenReturn(new Step());
        when(themeRepository.findAllById(List.of(1L, 2L))).thenReturn(List.of(theme(1L)));
        TravelDiary diary = new TravelDiary();
        User owner = user(111L);
        diary.setUser(owner);
        when(travelDiaryRepository.findById(200L)).thenReturn(Optional.of(diary));

        assertThatThrownBy(() -> stepService.createStep(input, 111L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("thèmes");

        verify(stepRepository, never()).save(any(Step.class));
    }

    @Test
    void updateStep_shouldAssignDistinctThemesFromIds() {
        StepRequestDTO update = new StepRequestDTO();
        update.setThemeIds(List.of(5L, 5L, 6L));

        Step existing = new Step();
        existing.setThemes(List.of(theme(1L)));
        TravelDiary diary = new TravelDiary();
        User owner = user(55L);
        diary.setUser(owner);
        existing.setTravelDiary(diary);

        Theme resolvedTheme = theme(5L);
        Theme secondResolvedTheme = theme(6L);
        StepResponseDTO mappedDto = new StepResponseDTO();

        when(stepRepository.findById(10L)).thenReturn(Optional.of(existing));
        when(themeRepository.findAllById(List.of(5L, 6L))).thenReturn(List.of(resolvedTheme, secondResolvedTheme));
        when(stepRepository.save(existing)).thenReturn(existing);
        when(stepMapper.toResponseDto(existing)).thenReturn(mappedDto);

        StepResponseDTO result = stepService.updateStep(10L, update, 55L);

        assertThat(existing.getThemes()).containsExactly(resolvedTheme, secondResolvedTheme);
        assertThat(result).isSameAs(mappedDto);
        verify(themeRepository).findAllById(List.of(5L, 6L));
    }

    @Test
    void updateLikes_shouldIncreaseCounter() {
        Step persisted = new Step();
        persisted.setLikesCount(2L);
        StepResponseDTO mappedDto = new StepResponseDTO();
        mappedDto.setLikesCount(3L);
        long currentUserId = 5L;

        when(stepRepository.findById(42L)).thenReturn(Optional.of(persisted));
        when(stepLikeRepository.findByStepIdAndUserId(42L, currentUserId)).thenReturn(Optional.empty());
        when(stepLikeRepository.countByStepId(42L)).thenReturn(3L);
        when(stepRepository.save(persisted)).thenReturn(persisted);
        when(stepMapper.toResponseDto(persisted)).thenReturn(mappedDto);

        StepResponseDTO result = stepService.updateLikes(42L, true, currentUserId);

        verify(stepLikeRepository).save(any(StepLike.class));
        verify(stepLikeRepository).countByStepId(42L);
        assertThat(persisted.getLikesCount()).isEqualTo(3L);
        assertThat(result).isSameAs(mappedDto);
    }

    @Test
    void updateLikes_shouldNotGoBelowZeroWhenDecrementing() {
        Step persisted = new Step();
        persisted.setLikesCount(0L);
        StepResponseDTO mappedDto = new StepResponseDTO();
        mappedDto.setLikesCount(0L);
        long currentUserId = 7L;

        when(stepRepository.findById(7L)).thenReturn(Optional.of(persisted));
        when(stepLikeRepository.findByStepIdAndUserId(7L, currentUserId)).thenReturn(Optional.empty());
        when(stepLikeRepository.countByStepId(7L)).thenReturn(0L);
        when(stepRepository.save(persisted)).thenReturn(persisted);
        when(stepMapper.toResponseDto(persisted)).thenReturn(mappedDto);

        StepResponseDTO result = stepService.updateLikes(7L, false, currentUserId);

        verify(stepLikeRepository, never()).delete(any(StepLike.class));
        verify(stepLikeRepository).countByStepId(7L);
        assertThat(persisted.getLikesCount()).isZero();
        assertThat(result).isSameAs(mappedDto);
    }

    @Test
    void updateLikes_shouldDecreaseCounterWhenUserDislikes() {
        Step persisted = new Step();
        persisted.setLikesCount(2L);
        StepResponseDTO mappedDto = new StepResponseDTO();
        mappedDto.setLikesCount(1L);
        long currentUserId = 11L;
        StepLike existingLike = new StepLike();
        existingLike.setStep(persisted);
        User u = user(currentUserId);
        existingLike.setUser(u);

        when(stepRepository.findById(9L)).thenReturn(Optional.of(persisted));
        when(stepLikeRepository.findByStepIdAndUserId(9L, currentUserId)).thenReturn(Optional.of(existingLike));
        when(stepLikeRepository.countByStepId(9L)).thenReturn(1L);
        when(stepRepository.save(persisted)).thenReturn(persisted);
        when(stepMapper.toResponseDto(persisted)).thenReturn(mappedDto);

        StepResponseDTO result = stepService.updateLikes(9L, false, currentUserId);

        verify(stepLikeRepository).delete(existingLike);
        verify(stepLikeRepository).countByStepId(9L);
        assertThat(persisted.getLikesCount()).isEqualTo(1L);
        assertThat(result).isSameAs(mappedDto);
    }

    private Theme theme(Long id) {
        Theme theme = new Theme();
        theme.setId(id);
        return theme;
    }

    private User user(Long id) {
        User user = new User();
        user.setId(id);
        user.setEmail("user" + id + "@mail.test");
        return user;
    }
}
