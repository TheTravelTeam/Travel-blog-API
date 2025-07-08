package com.wcs.travel_blog.repository;

import com.wcs.travel_blog.model.TravelDiary;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TravelDiaryRepository extends JpaRepository<TravelDiary, Long> {
    // Si tu veux tout charger directement (user et steps) via un query
    @EntityGraph(attributePaths = {"steps", "user"})
    List<TravelDiary> findAll();
}