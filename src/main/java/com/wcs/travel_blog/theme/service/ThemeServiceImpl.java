package com.wcs.travel_blog.theme.service;

import com.wcs.travel_blog.theme.dto.ThemeDTO;
import com.wcs.travel_blog.theme.mapper.ThemeMapper;
import com.wcs.travel_blog.theme.model.Theme;
import com.wcs.travel_blog.theme.repository.ThemeRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ThemeServiceImpl implements ThemeService {

    private final ThemeRepository themeRepository;
    private final ThemeMapper themeMapper;
    private final ThemeService themeService;

    public ThemeServiceImpl(ThemeRepository themeRepository, ThemeMapper themeMapper, ThemeService themeService) {
        this.themeRepository = themeRepository;
        this.themeMapper = themeMapper;
        this.themeService = themeService;
    }

    @Override
    public void createTheme(ThemeDTO themeDTO) {
        Theme theme = themeMapper.toEntity(themeDTO);
        themeRepository.save(theme);
    }

    @Override
    public ThemeDTO updateTheme(Long id, Theme theme) {


        return null;
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
}
