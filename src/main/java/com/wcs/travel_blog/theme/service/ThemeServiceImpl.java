package com.wcs.travel_blog.theme.service;

import com.wcs.travel_blog.theme.dto.ThemeDTO;
import com.wcs.travel_blog.theme.mapper.ThemeMapper;
import com.wcs.travel_blog.theme.model.Theme;
import com.wcs.travel_blog.theme.repository.ThemeRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ThemeServiceImpl implements ThemeService {
    private final ThemeRepository themeRepository;
    private final ThemeMapper themeMapper;

    public ThemeServiceImpl(ThemeRepository themeRepository, ThemeMapper themeMapper) {
        this.themeRepository = themeRepository;
        this.themeMapper = themeMapper;
    }

    @Override
    public ThemeDTO createTheme(ThemeDTO themeDTO) {
        Theme theme = themeMapper.toEntity(themeDTO);
        Theme themeSaved = themeRepository.save(theme);
        return themeMapper.toDto(themeSaved);
    }

    @Override
    public ThemeDTO updateTheme(Long id, ThemeDTO themeDTO) {
        Theme existingTheme = themeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Theme not found with id: " + id));
        existingTheme.setName(themeDTO.getName());
        existingTheme.setUpdatedAt(LocalDateTime.now());
        Theme updatedTheme = themeRepository.save(existingTheme);
        return themeMapper.toDto(updatedTheme);
    }

    @Override
    public List<ThemeDTO> getAllTheme() {
        List<Theme> themes = themeRepository.findAll();
        return themes.stream()
                .map(themeMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public ThemeDTO getThemeById(Long id) {
        Theme theme = themeRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("theme not found"));
        return themeMapper.toDto(theme);
    }

    @Override
    public void deleteTheme(Long id) {
        Theme theme = themeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Theme not found with id: " + id));
        themeRepository.delete(theme);
    }
}
