package com.wcs.travel_blog.step.repository;

import com.wcs.travel_blog.step.model.Step;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StepRepository extends JpaRepository<Step, Long> {
}
