package com.wcs.travel_blog.step.repository;

import com.wcs.travel_blog.step.model.Step;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface StepRepository extends JpaRepository<Step, Long> {

    /**
     * Recherche les étapes visibles pour l'utilisateur courant ou pour un visiteur anonyme.
     * Les étapes publiques proviennent de carnets publiés et non privés ;
     * les étapes privées ne sont renvoyées que pour le propriétaire du carnet associé.
     * @param query  fragment recherché
     * @param userId identifiant de l'utilisateur courant, {@code null} sinon
     * @return étapes triées par date de mise à jour décroissante
     */
    @Query("""
            SELECT DISTINCT step FROM Step step
            JOIN step.travelDiary td
            WHERE (
                LOWER(step.title) LIKE LOWER(CONCAT('%', :query, '%'))
                OR LOWER(step.description) LIKE LOWER(CONCAT('%', :query, '%'))
            )
            AND (
                (:userId IS NOT NULL AND td.user.id = :userId)
                OR (
                    td.isPublished = TRUE
                    AND (td.isPrivate IS NULL OR td.isPrivate = FALSE)
                )
            )
            ORDER BY step.updatedAt DESC
            """)
    List<Step> searchVisibleSteps(@Param("query") String query, @Param("userId") Long userId);
}
