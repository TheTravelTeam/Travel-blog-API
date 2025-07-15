package com.wcs.travel_blog.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class Step {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private LocalDateTime startDate;

    private LocalDateTime endDate;

    private Double latitude;

    private Double longitude;

    private String location;

    private String country;

    private String continent;

//    end_date
//    status
//    location
//    country
//    continent
//    latitude
//    longitude
//#travel_journal_Id
}
