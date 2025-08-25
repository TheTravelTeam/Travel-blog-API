package com.wcs.travel_blog.theme.mapper;

import com.wcs.travel_blog.theme.dto.ThemeDTO;
import com.wcs.travel_blog.theme.model.Theme;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

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

        LocalDateTime now = LocalDateTime.now();
        if (themeDTO.getId() == null) {
            theme.setCreatedAt(now);
        } else {
            theme.setUpdatedAt(theme.getCreatedAt() != null ? theme.getCreatedAt() : now);
        }
        theme.setUpdatedAt(now);

        return theme;
    }
}
