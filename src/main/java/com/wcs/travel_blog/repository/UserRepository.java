package com.wcs.travel_blog.repository;

import com.wcs.travel_blog.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
