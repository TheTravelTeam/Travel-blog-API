package com.wcs.travel_blog.article.dto;

import com.wcs.travel_blog.media.dto.MediaDTO;
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
    private String coverUrl;
    private List<MediaDTO> medias;
    private  LocalDateTime updatedAt;
    private String slug;
    private Long userId;
    private String pseudo;
    private List<ThemeDTO> themes;
}
