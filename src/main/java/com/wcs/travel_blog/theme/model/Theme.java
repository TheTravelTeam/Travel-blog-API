package com.wcs.travel_blog.theme.model;

import com.wcs.travel_blog.step.model.Step;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Entity
public class Theme {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now();

    @ManyToMany(mappedBy = "themes")
    private List<Step> steps;

    /**
     * permet de mettre à jour automatiquement (à voir)
     *
     @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }*/
}
