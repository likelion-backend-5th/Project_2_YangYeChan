package com.example.project_2_yangyechan.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "user")
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password;

    private String profile_img;

    private String email;

    private String phone;

    @OneToMany(mappedBy = "user")
    private List<CommentEntity> comments = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    private List<ArticleEntity> articles = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    private List<Like_ArticleEntity> like_articles = new ArrayList<>();
}
