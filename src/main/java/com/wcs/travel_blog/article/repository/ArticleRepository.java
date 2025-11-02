package com.wcs.travel_blog.article.repository;

import com.wcs.travel_blog.article.model.Article;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ArticleRepository extends JpaRepository<Article,Long> {
    Optional<Article> findBySlug(String slug);
}
