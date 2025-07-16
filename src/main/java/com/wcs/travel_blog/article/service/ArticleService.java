package com.wcs.travel_blog.article.service;

import com.wcs.travel_blog.article.dto.ArticleDTO;
import com.wcs.travel_blog.article.dto.CreateArticleDTO;

import com.wcs.travel_blog.article.mapper.ArticleMapper;
import com.wcs.travel_blog.article.model.Article;
import com.wcs.travel_blog.article.repository.ArticleRepository;
import com.wcs.travel_blog.user.model.User;
import com.wcs.travel_blog.user.repository.UserRepository;
import com.wcs.travel_blog.util.SlugUtil;
import jakarta.persistence.EntityNotFoundException;

import java.util.List;
import java.util.stream.Collectors;

public class ArticleService {
    private final ArticleRepository articleRepository;
    private final ArticleMapper articleMapper;
    private final UserRepository userRepository;

    public ArticleService(ArticleRepository articleRepository, ArticleMapper articleMapper, UserRepository userRepository) {
        this.articleRepository = articleRepository;
        this.articleMapper = articleMapper;
        this.userRepository = userRepository;
    }

    // READ ALL
    public List<ArticleDTO> getAllArticles() {
        List<Article> articles = articleRepository.findAll();
        return articles.stream().map(articleMapper::convertToDTO).collect(Collectors.toList());
    }

    // READ BY ID
    public ArticleDTO getArticleById(Long id) {
        Article article = articleRepository.findById(id).orElseThrow(()-> new EntityNotFoundException("article non trouvé"));
        return  articleMapper.convertToDTO(article);
    }

    // CREATE
    public ArticleDTO createArticle(CreateArticleDTO createArticleDTO) {
        if (createArticleDTO.getUserId() == null) {
            throw new IllegalArgumentException("L'Id de l'utilisateur est obligatoire");
        }
        User user= userRepository.findById(createArticleDTO.getUserId())
                .orElseThrow(() -> new EntityNotFoundException("utilisateur non trouvé"));

        Article article= articleMapper.convertToEntity(createArticleDTO, user);

        String generatedSlug= SlugUtil.slugify(createArticleDTO.getTitle());
        article.setSlug(generatedSlug);

        Article savedArticle = articleRepository.save(article);

        return articleMapper.convertToDTO(savedArticle);
    }

    //UPDATE
//    public  ArticleDTO updateArticle(Long articleId, Article )
}
