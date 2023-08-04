package com.example.project_2_yangyechan.dto;

import com.example.project_2_yangyechan.entity.UserEntity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ArticleDto {
    private Long id;

    private UserEntity user;

    private String title;

    private String content;

    private Boolean draft;

    private LocalDateTime deleted_at;

}
