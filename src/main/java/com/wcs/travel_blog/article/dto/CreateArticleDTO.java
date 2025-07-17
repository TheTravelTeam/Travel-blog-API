package com.wcs.travel_blog.article.dto;


import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class CreateArticleDTO {

    private String title;
    private String content;
    private Long userId;
}
