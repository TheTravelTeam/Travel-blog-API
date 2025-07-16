package com.wcs.travel_blog.theme.service;

import com.wcs.travel_blog.theme.dto.ThemeDTO;
import com.wcs.travel_blog.theme.mapper.ThemeMapper;
import com.wcs.travel_blog.theme.model.Theme;
import com.wcs.travel_blog.theme.repository.ThemeRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ThemeServiceImpl implements ThemeService {

    private final ThemeRepository themeRepository;
    private final ThemeMapper themeMapper;

    public ThemeServiceImpl(ThemeRepository themeRepository, ThemeMapper themeMapper) {
        this.themeRepository = themeRepository;
        this.themeMapper = themeMapper;
    }

    @Override
    public Theme createTheme(ThemeDTO themeDTO) {
        Theme theme = themeMapper.toEntity(themeDTO);
        return themeRepository.save(theme);
    }

    @Override
    public ThemeDTO updateTheme(Long id, Theme theme) {

        return null;
    }

    @Override
    public List<ThemeDTO> getAllTheme() {

        return List.of();
    }

    @Override
    public ThemeDTO getTheme(Long id) {

        return null;
    }
}
