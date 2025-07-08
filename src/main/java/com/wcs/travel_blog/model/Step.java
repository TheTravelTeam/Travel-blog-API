package com.wcs.travel_blog.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
public class Step {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    @Column(length = 1000)
    private String description;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();

    @Column(name = "start_date")
    private LocalDateTime startDate;

    @Column(name = "end_date")
    private LocalDateTime endDate;

    @Enumerated(EnumType.STRING)
    private Status status = Status.IN_PROGRESS;

    private String location;
    private String country;
    private String continent;
    private Double latitude;
    private Double longitude;

    @ManyToOne
    @JsonBackReference("diary-steps")
    @JoinColumn(name = "travel_diary_id")
    private TravelDiary travelDiary;

    @OneToMany(mappedBy = "step", cascade = CascadeType.ALL)
    @JsonManagedReference("step-medias")
    private List<Media> medias;

    @OneToMany(mappedBy = "step", cascade = CascadeType.ALL)
    @JsonManagedReference("step-comments")
    private List<Comment> comments;

    @OneToMany(mappedBy = "step", cascade = CascadeType.ALL)
    @JsonManagedReference("step-stepThemes")
    private List<StepTheme> stepThemes;

    public enum Status {
        IN_PROGRESS, COMPLETED, PRIVATE_STEP
    }

    public List<StepTheme> getStepThemes() {
        return stepThemes;
    }

    public void setStepThemes(List<StepTheme> stepThemes) {
        this.stepThemes = stepThemes;
    }

    public List<Comment> getComments() {
        return comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }

    public List<Media> getMedias() {
        return medias;
    }

    public void setMedias(List<Media> medias) {
        this.medias = medias;
    }

    public TravelDiary getTravelDiary() {
        return travelDiary;
    }

    public void setTravelDiary(TravelDiary travelDiary) {
        this.travelDiary = travelDiary;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public String getContinent() {
        return continent;
    }

    public void setContinent(String continent) {
        this.continent = continent;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public LocalDateTime getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDateTime endDate) {
        this.endDate = endDate;
    }

    public LocalDateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

}

