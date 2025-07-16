package com.wcs.travel_blog.article.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class UpdateArticleDTO {

    private Long id;
    private String title;
    private String content;
    private  LocalDateTime updatedAt;
}
