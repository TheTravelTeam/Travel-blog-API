package com.wcs.travel_blog.article.controller;

import com.wcs.travel_blog.article.dto.ArticleDTO;
import com.wcs.travel_blog.article.model.Article;
import com.wcs.travel_blog.article.repository.ArticleRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/articles")
public class ArticleController {

    private final ArticleRepository articleRepository;

    public ArticleController(ArticleRepository articleRepository) {
        this.articleRepository = articleRepository;
    }

    @GetMapping
    public ResponseEntity<List<Article>> getAllArticles() {
        List<Article> articles = articleRepository.findAll();
        if (articles.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(articles);
    }

//    @GetMapping("/{id}")
//    public ResponseEntity<Article> getArticleById(@PathVariable Long id) {
//        ArticleDTO article =
//    }
}
