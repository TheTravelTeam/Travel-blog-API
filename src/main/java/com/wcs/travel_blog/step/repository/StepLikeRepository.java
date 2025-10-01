package com.wcs.travel_blog.step.repository;

import com.wcs.travel_blog.step.model.StepLike;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StepLikeRepository extends JpaRepository<StepLike, Long> {

    Optional<StepLike> findByStepIdAndUserId(Long stepId, Long userId);

    long countByStepId(Long stepId);

    void deleteByStepIdAndUserId(Long stepId, Long userId);
}
