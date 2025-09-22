package com.wcs.travel_blog.article.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class UpdateArticleDTO {

    private String title;
    private String content;
    private Long userId;
    private List<Long> themeIds;
}
