package com.wcs.travel_blog.article.dto;


import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class CreateArticleDTO {

    @NotBlank
    private String title;
    private String content;
    private Long userId;
}
