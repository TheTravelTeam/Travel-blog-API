package com.wcs.travel_blog.media.repository;

import com.wcs.travel_blog.media.model.Media;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MediaRepository extends JpaRepository<Media, Long> {
    List<Media> findByStep_Id(Long stepId);
    List<Media> findByArticle_Id(Long articleId);
    Optional<Media> findByTravelDiary_Id(Long travelDiaryId);
    Optional<Media> findByPublicId(String publicId);
}
