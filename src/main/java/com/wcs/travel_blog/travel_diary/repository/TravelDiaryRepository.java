package com.wcs.travel_blog.travel_diary.repository;

import com.wcs.travel_blog.travel_diary.model.TravelDiary;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TravelDiaryRepository extends JpaRepository<TravelDiary, Long> {

    /**
     * Recherche les carnets visibles publiquement pour un visiteur ou un utilisateur authentifié.
     * Seuls les carnets publiés, non privés et possédant au moins une étape sont renvoyés.
     * @param query fragment recherché (non trimé ici pour conserver la flexibilité côté service)
     * @return liste de carnets triés par date de mise à jour décroissante
     */
    @Query("""
            SELECT DISTINCT td FROM TravelDiary td
            LEFT JOIN FETCH td.media media
            LEFT JOIN td.steps step
            WHERE (
                LOWER(td.title) LIKE LOWER(CONCAT('%', :query, '%'))
                OR LOWER(td.description) LIKE LOWER(CONCAT('%', :query, '%'))
                OR LOWER(step.city) LIKE LOWER(CONCAT('%', :query, '%'))
                OR LOWER(step.country) LIKE LOWER(CONCAT('%', :query, '%'))
                OR LOWER(step.continent) LIKE LOWER(CONCAT('%', :query, '%'))
            )
            AND COALESCE(td.isPrivate, FALSE) = FALSE
            AND td.isPublished = TRUE
            AND SIZE(td.steps) > 0
            AND (td.status IS NULL OR td.status <> com.wcs.travel_blog.travel_diary.model.TravelStatus.DISABLED)
            ORDER BY td.updatedAt DESC
            """)
    List<TravelDiary> searchVisibleDiaries(@Param("query") String query);

    @Query("""
            SELECT DISTINCT td FROM TravelDiary td
            LEFT JOIN FETCH td.media media
            WHERE td.isPrivate = FALSE
              AND td.isPublished = TRUE
              AND SIZE(td.steps) > 0
              AND (td.status IS NULL OR td.status <> com.wcs.travel_blog.travel_diary.model.TravelStatus.DISABLED)
            ORDER BY td.updatedAt DESC
            """)
    List<TravelDiary> findAllPublishedPublicWithSteps();

    @EntityGraph(attributePaths = "media")
    List<TravelDiary> findAllByUserIdOrderByUpdatedAtDesc(Long userId);

    @Query("""
            SELECT DISTINCT td FROM TravelDiary td
            LEFT JOIN FETCH td.media media
            WHERE td.user.id = :userId
              AND td.isPrivate = FALSE
              AND td.isPublished = TRUE
              AND SIZE(td.steps) > 0
              AND (td.status IS NULL OR td.status <> com.wcs.travel_blog.travel_diary.model.TravelStatus.DISABLED)
            ORDER BY td.updatedAt DESC
            """)
    List<TravelDiary> findPublishedPublicByUserId(@Param("userId") Long userId);

}
