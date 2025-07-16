package com.wcs.travel_blog.theme.service;

import com.wcs.travel_blog.theme.dto.ThemeDTO;
import com.wcs.travel_blog.theme.model.Theme;

import java.util.List;

public interface ThemeService {

    Theme createTheme(ThemeDTO themeDTO);
    ThemeDTO updateTheme(Long id, Theme theme);
    List<ThemeDTO> getAllTheme();
    ThemeDTO getTheme(Long id);
}
