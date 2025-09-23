package com.wcs.travel_blog.article.service;

import com.wcs.travel_blog.article.dto.ArticleDTO;
import com.wcs.travel_blog.article.dto.CreateArticleDTO;

import com.wcs.travel_blog.article.dto.UpdateArticleDTO;
import com.wcs.travel_blog.article.mapper.ArticleMapper;
import com.wcs.travel_blog.article.model.Article;
import com.wcs.travel_blog.article.repository.ArticleRepository;
import com.wcs.travel_blog.theme.model.Theme;
import com.wcs.travel_blog.theme.repository.ThemeRepository;
import com.wcs.travel_blog.user.model.User;
import com.wcs.travel_blog.user.repository.UserRepository;
import com.wcs.travel_blog.util.SlugUtil;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ArticleService {
    private final ArticleRepository articleRepository;
    private final ArticleMapper articleMapper;
    private final UserRepository userRepository;
    private final ThemeRepository themeRepository;

    public ArticleService(ArticleRepository articleRepository,
                          ArticleMapper articleMapper,
                          UserRepository userRepository,
                          ThemeRepository themeRepository) {
        this.articleRepository = articleRepository;
        this.articleMapper = articleMapper;
        this.userRepository = userRepository;
        this.themeRepository = themeRepository;
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

        List<Theme> themes = resolveThemes(createArticleDTO.getThemeIds());

        Article article= articleMapper.convertToEntity(createArticleDTO, user, themes);

        article.setCreatedAt(LocalDateTime.now());
        article.setUpdatedAt(LocalDateTime.now());

        String slug= SlugUtil.slugify(createArticleDTO.getTitle());
        article.setSlug(slug);

        Article savedArticle = articleRepository.save(article);

        return articleMapper.convertToDTO(savedArticle);
    }

    //UPDATE
    public  ArticleDTO updateArticle(Long articleId, UpdateArticleDTO updArticleDTO) {

        // Check that the article exists
        Article article = articleRepository.findById(articleId)
                .orElseThrow(() -> new EntityNotFoundException("Article non trouvé"));

        // Checks and updates the title and slug if modified
        if (updArticleDTO.getTitle() != null && !updArticleDTO.getTitle().equals(article.getTitle())) {
            article.setTitle(updArticleDTO.getTitle());
            article.setSlug(SlugUtil.slugify(updArticleDTO.getTitle()));
        }

        // Checks and updates the content if modified
        if (updArticleDTO.getContent() != null && !updArticleDTO.getContent().equals(article.getContent())) {
            article.setContent(updArticleDTO.getContent());
        }

        // Checks and updates the user (author of the article) if modified
        if (updArticleDTO.getUserId() != null && !updArticleDTO.getUserId().equals(article.getUser().getId())) {
            User user = userRepository.findById(updArticleDTO.getUserId())
                    .orElseThrow(() -> new EntityNotFoundException("Utilisateur non trouvé"));
            article.setUser(user);
        }

        if (updArticleDTO.getThemeIds() != null) {
            List<Theme> themes = resolveThemes(updArticleDTO.getThemeIds());
            article.setThemes(new java.util.ArrayList<>(themes));
        }

        //automatic LocalDateTime update
        article.setUpdatedAt(LocalDateTime.now());

        Article updatedArticle = articleRepository.save(article);

        return  articleMapper.convertToDTO(updatedArticle);
    }

    // DELETE
    public void deleteArticle(Long articleId) {

        Article article= articleRepository.findById(articleId)
                .orElseThrow(() -> new EntityNotFoundException("Article non trouvé"));

        articleRepository.delete(article);
    }

    private List<Theme> resolveThemes(List<Long> themeIds) {
        if (themeIds == null || themeIds.isEmpty()) {
            return List.of();
        }

        List<Theme> themes = themeRepository.findAllById(themeIds);
        if (themes.size() != themeIds.size()) {
            throw new EntityNotFoundException("Un ou plusieurs thèmes sont introuvables");
        }
        return themes;
    }
}
