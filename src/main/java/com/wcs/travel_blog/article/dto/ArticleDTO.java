package com.wcs.travel_blog.article.dto;

import com.wcs.travel_blog.theme.dto.ThemeDTO;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

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
    private List<ThemeDTO> themes;
}
