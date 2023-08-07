package com.example.project_2_yangyechan.repository;

import com.example.project_2_yangyechan.entity.ArticleEntity;
import com.example.project_2_yangyechan.entity.Like_ArticleEntity;
import com.example.project_2_yangyechan.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface Like_ArticleRepository extends JpaRepository<Like_ArticleEntity, Long> {
    //  사용자 계정이름으로 사용자 정보를 회수하는 기능
    Optional<Like_ArticleEntity> findByUserAndArticle(UserEntity user, ArticleEntity article);
    Optional<Like_ArticleEntity> deleteByUser(UserEntity user);
}
