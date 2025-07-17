package com.wcs.travel_blog.article.mapper;
import com.wcs.travel_blog.article.dto.ArticleDTO;
import com.wcs.travel_blog.article.dto.CreateArticleDTO;
import com.wcs.travel_blog.article.model.Article;
import com.wcs.travel_blog.user.model.User;
import org.springframework.stereotype.Component;


@Component
public class ArticleMapper {

    // convert to DTO
    public ArticleDTO convertToDTO(Article article) {
        ArticleDTO articleDTO = new ArticleDTO();
        articleDTO.setId(article.getId());
        articleDTO.setTitle(article.getTitle());
        articleDTO.setContent(article.getContent());
        articleDTO.setUpdatedAt(article.getUpdatedAt());
        articleDTO.setSlug(article.getSlug());
        if (article.getUser() != null) {
            articleDTO.setUserId(article.getUser().getId());
            articleDTO.setUsername(article.getUser().getUsername());
        }
        return articleDTO;
    }

    // convert to Entity
    public Article convertToEntity(CreateArticleDTO createArticleDTO,  User user) {
        Article article = new Article();
        article.setTitle(createArticleDTO.getTitle());
        article.setContent(createArticleDTO.getContent());
        article.setUser(user);
        return article;
    }
}

