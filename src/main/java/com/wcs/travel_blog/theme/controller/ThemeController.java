package com.wcs.travel_blog.theme.controller;

import com.wcs.travel_blog.theme.dto.ThemeDTO;
import com.wcs.travel_blog.theme.model.Theme;
import com.wcs.travel_blog.theme.service.ThemeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/theme")
public class ThemeController {

    private final ThemeService themeService;

    public ThemeController(ThemeService themeService) {
        this.themeService = themeService;
    }

    @PostMapping("/create")
    public void create(@RequestBody ThemeDTO themeDTO) {
        themeService.createTheme(themeDTO);
    }

    @GetMapping
    public ResponseEntity<List<ThemeDTO>> getAllTheme() {
        List<ThemeDTO> themeDTOList = themeService.getAllTheme();
        if (themeDTOList.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(themeDTOList);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ThemeDTO> getTheme(@PathVariable Long id) {
        ThemeDTO themeDTO = themeService.getThemeById(id);
        if (themeDTO == null)
            return ResponseEntity.notFound().build();
        return ResponseEntity.ok(themeDTO);
    }

}
