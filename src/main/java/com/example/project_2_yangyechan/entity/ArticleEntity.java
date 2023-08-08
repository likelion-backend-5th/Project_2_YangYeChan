package com.example.project_2_yangyechan.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "article")
public class ArticleEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity user;

    private String title;

    private String content;

    private Boolean draft;

    private LocalDateTime deleted_at;

    @OneToMany(mappedBy = "article")
    private List<CommentEntity> comments = new ArrayList<>();

    @OneToMany(mappedBy = "article")
    private List<Like_ArticleEntity> like_articles = new ArrayList<>();

    @OneToMany(mappedBy = "article")
    private List<Article_ImagesEntity> article_images = new ArrayList<>();

}
