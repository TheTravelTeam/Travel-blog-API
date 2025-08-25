package com.wcs.travel_blog.theme.service;

import com.wcs.travel_blog.theme.dto.ThemeDTO;

import java.util.List;

public interface ThemeService {

    ThemeDTO createTheme(ThemeDTO themeDTO);
    ThemeDTO updateTheme(Long id, ThemeDTO themeDTO);
    List<ThemeDTO> getAllTheme();
    ThemeDTO getThemeById(Long id);
    void deleteTheme(Long id);
}
