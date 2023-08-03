package com.example.project_2_yangyechan.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "like_article")
public class Like_ArticleEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity user;

    @ManyToOne
    @JoinColumn(name = "article_id")
    private ArticleEntity article;
}
