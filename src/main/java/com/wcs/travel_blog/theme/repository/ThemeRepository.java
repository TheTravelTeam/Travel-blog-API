package com.wcs.travel_blog.theme.repository;

import com.wcs.travel_blog.theme.model.Theme;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ThemeRepository extends JpaRepository<Theme, Long> {
}
