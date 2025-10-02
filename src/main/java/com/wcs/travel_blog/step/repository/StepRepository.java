package com.wcs.travel_blog.step.repository;

import com.wcs.travel_blog.step.model.Step;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface StepRepository extends JpaRepository<Step, Long> {

    /**
     * Recherche les étapes appartenant à des carnets publics et publiés.
     * @param query fragment recherché
     * @return étapes triées par date de mise à jour décroissante
     */
    @Query("""
            SELECT DISTINCT step FROM Step step
            JOIN step.travelDiary td
            WHERE (
                LOWER(step.title) LIKE LOWER(CONCAT('%', :query, '%'))
                OR LOWER(step.description) LIKE LOWER(CONCAT('%', :query, '%'))
            )
            AND td.isPublished = TRUE
            AND COALESCE(td.isPrivate, FALSE) = FALSE
            ORDER BY step.updatedAt DESC
            """)
    List<Step> searchVisibleSteps(@Param("query") String query);
}
