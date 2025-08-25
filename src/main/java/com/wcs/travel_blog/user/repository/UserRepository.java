package com.wcs.travel_blog.user.repository;

import com.wcs.travel_blog.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
