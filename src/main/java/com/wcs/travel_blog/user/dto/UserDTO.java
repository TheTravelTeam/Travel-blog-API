package com.wcs.travel_blog.user.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserDTO {
    private Long id;
    private String username;
    private String avatar;
    private String email;
    private String biography;
    private String status;
}
