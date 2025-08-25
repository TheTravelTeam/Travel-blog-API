package com.wcs.travel_blog.media.repository;

import com.wcs.travel_blog.media.model.Media;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MediaRepository extends JpaRepository<Media, Long> {
}
