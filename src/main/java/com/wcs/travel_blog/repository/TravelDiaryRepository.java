package com.wcs.travel_blog.repository;

import com.wcs.travel_blog.model.TravelDiary;
import com.wcs.travel_blog.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TravelDiaryRepository extends JpaRepository<TravelDiary, Long> {
}
