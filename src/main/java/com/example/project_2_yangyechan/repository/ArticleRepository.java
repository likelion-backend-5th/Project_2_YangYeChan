package com.example.project_2_yangyechan.repository;

import com.example.project_2_yangyechan.entity.ArticleEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ArticleRepository extends JpaRepository<ArticleEntity, Long> {
}
