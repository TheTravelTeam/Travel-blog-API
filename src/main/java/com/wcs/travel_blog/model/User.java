package com.wcs.travel_blog.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String avatar;

    @Column(length = 50)
    private String username;

    @Column(nullable = false, length = 100, unique = true)
    private String email;

    @Column(nullable = false, length = 20)
    private String password;

    @Column(nullable = false)
    private LocalDateTime logginAt = LocalDateTime.now();

    @Column(columnDefinition = "TEXT")
    private String biography;

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();;

    @Column(nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now();;

    @Enumerated(EnumType.STRING)
    private UserStatus status = UserStatus.ACTIVE;

    @ElementCollection(fetch = FetchType.EAGER)
    private Set<String> roles = new HashSet<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<TravelDiary> travel_diaries;

    @OneToMany(mappedBy = "user")
    private List<Article> articles;

    @OneToMany(mappedBy = "user")
    private List<Comment> comments;

}
