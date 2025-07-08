package com.wcs.travel_blog.repository;

import com.wcs.travel_blog.model.Media;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MediaRepository extends JpaRepository<Media, Long> {
}
