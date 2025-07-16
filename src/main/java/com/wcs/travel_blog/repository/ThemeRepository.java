package com.wcs.travel_blog.repository;

import com.wcs.travel_blog.model.Theme;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ThemeRepository extends JpaRepository<Theme, Long> {
}
