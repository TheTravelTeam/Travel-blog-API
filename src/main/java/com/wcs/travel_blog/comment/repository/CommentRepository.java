package com.wcs.travel_blog.comment.repository;

import com.wcs.travel_blog.comment.model.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {
}
