package com.wcs.travel_blog.mapper;

import com.wcs.travel_blog.dto.ArticleDto;
import com.wcs.travel_blog.model.Article;

public class ArticleMapper {
    public static ArticleDto toDTO(Article article) {
        if (article == null) return null;

        ArticleDto dto = new ArticleDto();
        dto.setId(article.getId());
        dto.setTitle(article.getTitle());
        dto.setContent(article.getContent());
        dto.setSlug(article.getSlug());
        dto.setCreatedAt(article.getCreatedAt());
        dto.setUpdatedAt(article.getUpdatedAt());
        dto.setUser(UserMapper.mapUserToDto(article.getUser()));
        return dto;
    }
}
