package com.wcs.travel_blog.theme.mapper;

import com.wcs.travel_blog.theme.dto.ThemeDTO;
import com.wcs.travel_blog.theme.model.Theme;
import org.springframework.stereotype.Component;

@Component
public class ThemeMapper {

    public ThemeDTO toDto(Theme theme) {
        if (theme == null)
            return null;

        ThemeDTO themeDTO = new ThemeDTO();
        themeDTO.setId(theme.getId());
        themeDTO.setName(theme.getName());
        themeDTO.setUpdatedAt(theme.getUpdatedAt());
        return themeDTO;
    }

    public Theme toEntity(ThemeDTO themeDTO) {
        if (themeDTO == null)
            return null;

        Theme theme = new Theme();
        theme.setId(themeDTO.getId());
        theme.setName(themeDTO.getName());

        return theme;
    }
}
