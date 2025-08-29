package com.wcs.travel_blog.comment.repository;

import com.wcs.travel_blog.comment.model.Comment;
import com.wcs.travel_blog.comment.model.CommentStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByStep_Id(Long stepId);
    List<Comment> findByUser_Id(Long userId);
    long countByStep_IdAndStatus(Long stepId, CommentStatus status);
    boolean existsByIdAndUser_Id(Long commentId, Long userId);
}
