package com.wcs.travel_blog.article.mapper;

import com.wcs.travel_blog.article.dto.ArticleDTO;
import com.wcs.travel_blog.article.dto.CreateArticleDTO;
import com.wcs.travel_blog.article.model.Article;
import com.wcs.travel_blog.media.mapper.MediaMapper;
import com.wcs.travel_blog.theme.mapper.ThemeMapper;
import com.wcs.travel_blog.theme.model.Theme;
import com.wcs.travel_blog.user.model.User;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class ArticleMapper {

    private final ThemeMapper themeMapper;
    private final MediaMapper mediaMapper;

    public ArticleMapper(ThemeMapper themeMapper, MediaMapper mediaMapper) {
        this.themeMapper = themeMapper;
        this.mediaMapper = mediaMapper;
    }

    // convert to DTO
    public ArticleDTO convertToDTO(Article article) {
        ArticleDTO articleDTO = new ArticleDTO();
        articleDTO.setId(article.getId());
        articleDTO.setTitle(article.getTitle());
        articleDTO.setContent(article.getContent());
        articleDTO.setCoverUrl(article.getCoverUrl());
        articleDTO.setMedias(article.getMedias() != null
                ? article.getMedias().stream()
                .map(mediaMapper::toDto)
                .collect(Collectors.toList())
                : List.of());
        articleDTO.setUpdatedAt(article.getUpdatedAt());
        articleDTO.setSlug(article.getSlug());
        if (article.getUser() != null) {
            articleDTO.setUserId(article.getUser().getId());
            articleDTO.setPseudo(article.getUser().getPseudo());
        }
        articleDTO.setThemes(article.getThemes() != null
                ? article.getThemes().stream().map(themeMapper::toDto).collect(Collectors.toList())
                : List.of());
        return articleDTO;
    }

    // convert to Entity
    public Article convertToEntity(CreateArticleDTO createArticleDTO, User user, List<Theme> themes) {
        Article article = new Article();
        article.setTitle(createArticleDTO.getTitle());
        article.setContent(createArticleDTO.getContent());
        article.setCoverUrl(createArticleDTO.getCoverUrl());
        article.setMedias(new ArrayList<>());
        article.setUser(user);
        article.setThemes(themes != null ? new ArrayList<>(themes) : new ArrayList<>());
        return article;
    }
}
