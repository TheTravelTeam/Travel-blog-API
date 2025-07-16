package com.wcs.travel_blog.media.model;

import com.wcs.travel_blog.step.model.Step;
import com.wcs.travel_blog.travel_diary.model.TravelDiary;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity

@Getter
@Setter
public class Media {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;

    @Column(length = 250)
    private String fileUrl;

    @Enumerated(EnumType.STRING)
    private MediaType mediaType;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    private Boolean isVisible;

    @OneToOne
    @JoinColumn(name="travel_diary_id")
    private TravelDiary travelDiary;

    @ManyToOne
    @JoinColumn(name = "step_id")
    private Step step;

}
