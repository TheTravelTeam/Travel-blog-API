package com.wcs.travel_blog.article.controller;

import com.wcs.travel_blog.article.dto.ArticleDTO;
import com.wcs.travel_blog.article.dto.CreateArticleDTO;
import com.wcs.travel_blog.article.dto.UpdateArticleDTO;
import com.wcs.travel_blog.article.model.Article;
import com.wcs.travel_blog.article.repository.ArticleRepository;
import com.wcs.travel_blog.article.service.ArticleService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/articles")
public class ArticleController {

    private final ArticleService articleService;

    public ArticleController( ArticleService articleService) {
        this.articleService = articleService;
    }

    // READ ALL
    @GetMapping
    public ResponseEntity<List<ArticleDTO>> getAllArticles() {
        List<ArticleDTO> articles = articleService.getAllArticles();
        if (articles.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(articles);
    }

    // READ BY ID
    @GetMapping("/{articleId}")
    public ResponseEntity<ArticleDTO> getArticleById(@PathVariable Long articleId) {
        ArticleDTO article = articleService.getArticleById(articleId);
        return ResponseEntity.ok(article);
    }

    // READ BY SLUG
    @GetMapping("/slug/{slug}")
    public ResponseEntity<ArticleDTO> getArticleBySlug(@PathVariable String slug) {
        ArticleDTO article = articleService.getArticleBySlug(slug);
        return ResponseEntity.ok(article);
    }

    // CREATE
    @PostMapping
    public ResponseEntity<ArticleDTO> createArticle(@RequestBody CreateArticleDTO createArticleDTO) {
        ArticleDTO savedArticle= articleService.createArticle(createArticleDTO);
        return  ResponseEntity.status(HttpStatus.CREATED).body(savedArticle);
    }

    @PutMapping("{articleId}")
    public ResponseEntity<ArticleDTO> updateArticle(@PathVariable Long articleId, @RequestBody UpdateArticleDTO updateArticleDTO) {
        ArticleDTO updatedArticle = articleService.updateArticle(articleId, updateArticleDTO);
        return  ResponseEntity.ok(updatedArticle);
    }

    @DeleteMapping("{articleId}")
    public ResponseEntity<Void> deleteArticle(@PathVariable Long articleId) {
        articleService.deleteArticle(articleId);
        return ResponseEntity.noContent().build();
    }

}
