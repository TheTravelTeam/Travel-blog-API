package com.wcs.travel_blog.travel_diary.repository;

import com.wcs.travel_blog.travel_diary.model.TravelDiary;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TravelDiaryRepository extends JpaRepository<TravelDiary, Long> {
}
