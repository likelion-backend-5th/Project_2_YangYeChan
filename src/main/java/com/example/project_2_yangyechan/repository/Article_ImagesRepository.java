package com.example.project_2_yangyechan.repository;

import com.example.project_2_yangyechan.entity.ArticleEntity;
import com.example.project_2_yangyechan.entity.Article_ImagesEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface Article_ImagesRepository extends JpaRepository<Article_ImagesEntity, Long> {
    List<Article_ImagesEntity> findAllByArticle(ArticleEntity articleid);
    Optional<Article_ImagesEntity> findByArticle(ArticleEntity article);
}

