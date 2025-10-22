package com.wcs.travel_blog.article.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CreateArticleDTO {

    @NotBlank
    private String title;
    private String content;
    private String coverUrl;
    private List<Long> mediaIds;
    private Long userId;
    private List<Long> themeIds;
}
