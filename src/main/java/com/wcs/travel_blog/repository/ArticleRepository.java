package com.wcs.travel_blog.repository;

import com.wcs.travel_blog.model.Article;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ArticleRepository extends JpaRepository<Article, Long> {
}