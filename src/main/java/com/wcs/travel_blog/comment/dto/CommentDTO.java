package com.wcs.travel_blog.comment.dto;

import com.wcs.travel_blog.comment.model.CommentStatus;
import com.wcs.travel_blog.user.dto.UserDTO;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Setter
@Getter
public class CommentDTO {

    private Long id;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    @DateTimeFormat(pattern = "dd-MM-yyyy HH:mm")
    private LocalDateTime createdAt;

    @DateTimeFormat(pattern = "dd-MM-yyyy HH:mm")
    private LocalDateTime updatedAt;

    private CommentStatus status;


    private UserDTO user;

}
