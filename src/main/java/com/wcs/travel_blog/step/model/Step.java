package com.wcs.travel_blog.step.model;

import com.wcs.travel_blog.comment.model.Comment;
import com.wcs.travel_blog.media.model.Media;
import com.wcs.travel_blog.theme.model.Theme;
import com.wcs.travel_blog.travel_diary.model.TravelDiary;
import com.wcs.travel_blog.travel_diary.model.TravelStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Setter
@Getter
@Entity
public class Step {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private LocalDateTime createdAt= LocalDateTime.now();

    @Column(nullable = false)
    private LocalDateTime updatedAt= LocalDateTime.now();

    @DateTimeFormat(pattern = "dd-MM-yyyy")
    private LocalDate startDate;

    @DateTimeFormat(pattern = "dd-MM-yyyy")
    private LocalDate endDate;

    @Enumerated(EnumType.STRING)
    private TravelStatus status;

    private Double latitude;

    private Double longitude;

    @Column(nullable = false, length = 100)
    private String city;

    @Column(nullable = false, length = 100)
    private String country;

    @Column(nullable = false, length = 100)
    private String continent;

    @ManyToOne
    @JoinColumn(name = "travel_diary_id")
    private TravelDiary travelDiary;

    @OneToMany(mappedBy = "step", cascade = CascadeType.ALL)
    private List<Media> medias;

    @OneToMany(mappedBy = "step", cascade = CascadeType.ALL)
    private List<Comment> comments;

    @ManyToMany
    @JoinTable(
       name = "step_theme",
       joinColumns = @JoinColumn(name = "step_id"),
       inverseJoinColumns = @JoinColumn(name = "theme_id")
    )
    private List<Theme> themes;

}
