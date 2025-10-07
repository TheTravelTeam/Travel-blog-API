package com.wcs.travel_blog.article.service;

import com.wcs.travel_blog.article.dto.ArticleDTO;
import com.wcs.travel_blog.article.dto.CreateArticleDTO;
import com.wcs.travel_blog.article.dto.UpdateArticleDTO;
import com.wcs.travel_blog.article.mapper.ArticleMapper;
import com.wcs.travel_blog.article.model.Article;
import com.wcs.travel_blog.article.repository.ArticleRepository;
import com.wcs.travel_blog.media.model.Media;
import com.wcs.travel_blog.media.repository.MediaRepository;
import com.wcs.travel_blog.user.model.User;
import com.wcs.travel_blog.user.repository.UserRepository;
import com.wcs.travel_blog.util.SlugUtil;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class ArticleService {
    private final ArticleRepository articleRepository;
    private final ArticleMapper articleMapper;
    private final UserRepository userRepository;
    private final MediaRepository mediaRepository;

    public ArticleService(ArticleRepository articleRepository,
                          ArticleMapper articleMapper,
                          UserRepository userRepository,
                          MediaRepository mediaRepository) {
        this.articleRepository = articleRepository;
        this.articleMapper = articleMapper;
        this.userRepository = userRepository;
        this.mediaRepository = mediaRepository;
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

        article.setCreatedAt(LocalDateTime.now());
        article.setUpdatedAt(LocalDateTime.now());

        String slug= SlugUtil.slugify(createArticleDTO.getTitle());
        article.setSlug(slug);

        if (createArticleDTO.getMediaIds() != null && !createArticleDTO.getMediaIds().isEmpty()) {
            List<Media> medias = resolveMedias(createArticleDTO.getMediaIds());
            medias.forEach(media -> media.setArticle(article));
            article.setMedias(medias);
        }

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

        if (updArticleDTO.getCoverUrl() != null && !updArticleDTO.getCoverUrl().equals(article.getCoverUrl())) {
            article.setCoverUrl(updArticleDTO.getCoverUrl());
        }

        if (updArticleDTO.getMediaIds() != null) {
            if (article.getMedias() != null) {
                article.getMedias().forEach(media -> media.setArticle(null));
            }
            List<Media> medias = resolveMedias(updArticleDTO.getMediaIds());
            medias.forEach(media -> media.setArticle(article));
            article.setMedias(medias);
        }

        // Checks and updates the user (author of the article) if modified
        if (updArticleDTO.getUserId() != null && !updArticleDTO.getUserId().equals(article.getUser().getId())) {
            User user = userRepository.findById(updArticleDTO.getUserId())
                    .orElseThrow(() -> new EntityNotFoundException("Utilisateur non trouvé"));
            article.setUser(user);
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

    private List<Media> resolveMedias(List<Long> mediaIds) {
        if (mediaIds == null || mediaIds.isEmpty()) {
            return new ArrayList<>();
        }

        List<Long> distinctIds = mediaIds.stream()
                .filter(Objects::nonNull)
                .collect(Collectors.collectingAndThen(
                        Collectors.toCollection(LinkedHashSet::new),
                        ArrayList::new
                ));

        if (distinctIds.isEmpty()) {
            return new ArrayList<>();
        }

        List<Media> medias = mediaRepository.findAllById(distinctIds);
        Map<Long, Media> mediasById = medias.stream()
                .collect(Collectors.toMap(Media::getId, Function.identity()));

        if (mediasById.size() != distinctIds.size()) {
            throw new EntityNotFoundException("Un ou plusieurs médias sont introuvables");
        }

        return distinctIds.stream()
                .map(mediasById::get)
                .collect(Collectors.toCollection(ArrayList::new));
    }
}
