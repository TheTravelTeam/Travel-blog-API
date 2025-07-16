package com.wcs.travel_blog.article.repository;

import com.wcs.travel_blog.article.model.Article;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ArticleRepository extends JpaRepository<Article,Long> {
}
