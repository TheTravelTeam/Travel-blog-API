package com.wcs.travel_blog.user.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Set;

@Getter
@Setter
public class UserDTO {
    private Long id;
    private String pseudo;
    private String avatar;
    private String email;
    private String biography;
    private String status;
    private Set<String> roles;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
