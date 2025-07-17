package com.wcs.travel_blog.theme.controller;

import com.wcs.travel_blog.theme.dto.ThemeDTO;
import com.wcs.travel_blog.theme.service.ThemeService;
import jakarta.validation.Valid;
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
    public void create(@Valid @RequestBody ThemeDTO themeDTO) {
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

    @PutMapping("/update/{id}")
    public ResponseEntity<ThemeDTO> updateTheme(@PathVariable Long id, @Valid @RequestBody ThemeDTO themeDTO) {
        ThemeDTO updatedTheme = themeService.updateTheme(id, themeDTO);
        if (updatedTheme == null)
            return ResponseEntity.notFound().build();
        return ResponseEntity.ok(updatedTheme);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTheme(@PathVariable Long id) {
        try {
            themeService.deleteTheme(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
}
