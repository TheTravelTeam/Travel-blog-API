package com.wcs.travel_blog.model;

import jakarta.persistence.Entity;

import java.time.LocalDateTime;

@Entity
public class User {

    private String id;
    private String avatar;
    private String username;
    private String email;
    private String password;
    private LocalDateTime logginAt;
    private String biography;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private 
}
