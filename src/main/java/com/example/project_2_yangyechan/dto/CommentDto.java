package com.example.project_2_yangyechan.dto;

import com.example.project_2_yangyechan.entity.ArticleEntity;
import com.example.project_2_yangyechan.entity.CommentEntity;
import com.example.project_2_yangyechan.entity.UserEntity;
import lombok.Data;

@Data
public class CommentDto {
    private String content;
    private UserEntity user;
    private ArticleEntity article;

    public static CommentDto fromEntity (CommentEntity entity) {
        CommentDto dto = new CommentDto();
        dto.setContent(entity.getContent());
        dto.setUser(entity.getUser());
        dto.setArticle(entity.getArticle());
        return dto;
    }
}
