package com.wcs.travel_blog.step.service;

import com.wcs.travel_blog.exception.ResourceNotFoundException;
import com.wcs.travel_blog.step.dto.StepRequestDTO;
import com.wcs.travel_blog.step.dto.StepResponseDTO;
import com.wcs.travel_blog.step.mapper.StepMapper;
import com.wcs.travel_blog.step.model.Step;
import com.wcs.travel_blog.step.repository.StepRepository;
import com.wcs.travel_blog.theme.model.Theme;
import com.wcs.travel_blog.theme.repository.ThemeRepository;
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

    @InjectMocks
    private StepService stepService;

    @Test
    void createStep_shouldAssignThemesFromIds() {
        StepRequestDTO input = new StepRequestDTO();
        input.setThemeIds(List.of(1L, 2L));

        Step mappedEntity = new Step();
        Theme theme1 = theme(1L);
        Theme theme2 = theme(2L);
        StepResponseDTO mappedDto = new StepResponseDTO();

        when(stepMapper.toEntity(input)).thenReturn(mappedEntity);
        when(themeRepository.findAllById(List.of(1L, 2L))).thenReturn(List.of(theme1, theme2));
        when(stepRepository.save(any(Step.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(stepMapper.toResponseDto(any(Step.class))).thenReturn(mappedDto);

        StepResponseDTO result = stepService.createStep(input);

        ArgumentCaptor<Step> captor = ArgumentCaptor.forClass(Step.class);
        verify(stepRepository).save(captor.capture());
        Step saved = captor.getValue();

        assertThat(saved.getThemes()).containsExactly(theme1, theme2);
        assertThat(result).isSameAs(mappedDto);
    }

    @Test
    void createStep_shouldThrowWhenThemeMissing() {
        StepRequestDTO input = new StepRequestDTO();
        input.setThemeIds(List.of(1L, 2L));

        when(stepMapper.toEntity(input)).thenReturn(new Step());
        when(themeRepository.findAllById(List.of(1L, 2L))).thenReturn(List.of(theme(1L)));

        assertThatThrownBy(() -> stepService.createStep(input))
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

        Theme resolvedTheme = theme(5L);
        Theme secondResolvedTheme = theme(6L);
        StepResponseDTO mappedDto = new StepResponseDTO();

        when(stepRepository.findById(10L)).thenReturn(Optional.of(existing));
        when(themeRepository.findAllById(List.of(5L, 6L))).thenReturn(List.of(resolvedTheme, secondResolvedTheme));
        when(stepRepository.save(existing)).thenReturn(existing);
        when(stepMapper.toResponseDto(existing)).thenReturn(mappedDto);

        StepResponseDTO result = stepService.updateStep(10L, update);

        assertThat(existing.getThemes()).containsExactly(resolvedTheme, secondResolvedTheme);
        assertThat(result).isSameAs(mappedDto);
        verify(themeRepository).findAllById(List.of(5L, 6L));
    }

    private Theme theme(Long id) {
        Theme theme = new Theme();
        theme.setId(id);
        return theme;
    }
}
