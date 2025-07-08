package com.wcs.travel_blog.repository;

import com.wcs.travel_blog.model.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {
}
