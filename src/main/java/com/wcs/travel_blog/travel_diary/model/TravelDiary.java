package com.wcs.travel_blog.travel_diary.model;

import com.wcs.travel_blog.media.model.Media;
import com.wcs.travel_blog.user.model.User;
import com.wcs.travel_blog.step.model.Step;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Entity

@Getter
@Setter
public class TravelDiary {

    private Long id;


    private String title;

    private String description;


    private LocalDateTime createdAt;


    private LocalDateTime updatedAt;

    private Boolean isPrivate;

    private Boolean isPublished;

    @Enumerated(EnumType.STRING)
    private TravelStatus status;

    private Boolean canComment;

    private Double latitude;

    private Double longitude;

    @OneToMany(mappedBy = "travelDiary")
    private List<Step> steps;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @OneToOne(mappedBy = "travelDiary")
    private Media media;

}