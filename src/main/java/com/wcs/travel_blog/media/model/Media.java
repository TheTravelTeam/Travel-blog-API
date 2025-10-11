package com.wcs.travel_blog.media.model;

import com.wcs.travel_blog.article.model.Article;
import com.wcs.travel_blog.step.model.Step;
import com.wcs.travel_blog.travel_diary.model.TravelDiary;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class Media {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(length = 1024, nullable = false)
    private String fileUrl;

    @Column(length = 255)
    private String publicId;

    //nullable = false : contrainte au niveau base de données (DDL).
    //→ Génère une colonne NOT NULL dans ta table.
    //@NotNull (Bean Validation – Jakarta Validation) : contrainte au niveau Java / validation.
    //→ Permet de lever une erreur avant même de parler à la base
    // (exemple : quand tu reçois un DTO via un contrôleur et que tu veux valider l’entité avant persist).
    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private MediaType mediaType;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    private Boolean isVisible;

    //LAZY : Hibernate charge seulement l’ID du TravelDiary au début ; si tu veux accéder à media.getTravelDiary(),
    // Hibernate fera une requête SQL supplémentaire à ce moment-là (proxy).
    //👉 Donc LAZY est meilleur pour éviter de surcharger avec des requêtes inutiles quand tu n’as pas besoin de l’objet lié.
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="travel_diary_id")
    private TravelDiary travelDiary;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "step_id")
    private Step step;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "article_id")
    private Article article;

}
