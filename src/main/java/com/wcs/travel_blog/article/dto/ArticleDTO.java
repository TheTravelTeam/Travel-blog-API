package com.wcs.travel_blog.article.dto;

import com.wcs.travel_blog.user.model.User;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ArticleDTO {
    private Long id;
    private String title;
    private String content;
    private  LocalDateTime updatedAt;
    private String slug;
    private Long userId;
    private String pseudo;
}
