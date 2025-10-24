package com.wcs.travel_blog.travel_diary.model;

import com.wcs.travel_blog.media.model.Media;
import com.wcs.travel_blog.user.model.User;
import com.wcs.travel_blog.step.model.Step;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity

@Getter
@Setter
public class TravelDiary {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;

    @Column(nullable=false, length=50)
    private String title;

    private String description;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    private LocalDate startDate;

    private LocalDate endDate;

    private Boolean isPrivate;

    private Boolean isPublished;

    @Enumerated(EnumType.STRING)
    private TravelStatus status;

    private Boolean canComment;

    private Double latitude;

    private Double longitude;

    @OneToMany(mappedBy = "travelDiary", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Step> steps;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @OneToOne(mappedBy = "travelDiary", cascade = CascadeType.ALL, orphanRemoval = true)
    private Media media;

}
