package com.wcs.travel_blog.travel_diary.repository;

import com.wcs.travel_blog.travel_diary.model.TravelDiary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TravelDiaryRepository extends JpaRepository<TravelDiary, Long> {

    /**
     * Recherche les carnets visibles pour l'utilisateur courant ou pour un visiteur anonyme.
     * Un carnet est renvoyé s'il correspond au texte recherché et que :
     * <ul>
     *     <li>il appartient au visiteur authentifié (cas owner), ou</li>
     *     <li>il est publié et non privé (cas public).</li>
     * </ul>
     * @param query  fragment recherché (non trimé ici pour conserver la flexibilité dans les repositories)
     * @param userId identifiant de l'utilisateur courant, {@code null} pour un visiteur anonyme
     * @return liste de carnets triés par date de mise à jour décroissante
     */
    @Query("""
            SELECT DISTINCT td FROM TravelDiary td
            LEFT JOIN FETCH td.media media
            WHERE (
                LOWER(td.title) LIKE LOWER(CONCAT('%', :query, '%'))
                OR LOWER(td.description) LIKE LOWER(CONCAT('%', :query, '%'))
            )
            AND (
                (:userId IS NOT NULL AND td.user.id = :userId)
                OR (
                    (td.isPrivate IS NULL OR td.isPrivate = FALSE)
                    AND td.isPublished = TRUE
                )
            )
            ORDER BY td.updatedAt DESC
            """)
    List<TravelDiary> searchVisibleDiaries(@Param("query") String query, @Param("userId") Long userId);

}
