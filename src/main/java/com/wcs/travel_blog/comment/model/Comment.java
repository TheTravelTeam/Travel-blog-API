package com.wcs.travel_blog.comment.model;

import com.wcs.travel_blog.step.model.Step;
import com.wcs.travel_blog.user.model.User;
import jakarta.persistence.Entity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Entity

@Getter
@Setter
public class Comment {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    @DateTimeFormat(pattern = "dd-MM-yyyy HH:mm")
    private LocalDateTime createdAt;

    @DateTimeFormat(pattern = "dd-MM-yyyy HH:mm")
    private LocalDateTime updatedAt;

    private commentStatus status;

    public enum commentStatus {
        APPROVED, PENDING, REJECTED
    }

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "step_id")
    private Step step;
}
