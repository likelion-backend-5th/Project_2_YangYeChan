package com.example.project_2_yangyechan.dto;

import com.example.project_2_yangyechan.entity.ArticleEntity;
import com.example.project_2_yangyechan.entity.Article_ImagesEntity;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;


@Data
public class RespondArticlePageDto {
    private String username;

    private String title;

    private String image_url;

    public static RespondArticlePageDto fromEntity(ArticleEntity entity) {
        if (entity.getDeleted_at() != null) {
            // Deleted_at 필드가 null이 아니라면 DTO를 반환하지 않음
            return null;
        }
        RespondArticlePageDto dto = new RespondArticlePageDto();
        dto.setUsername(entity.getUser().getUsername());
        dto.setTitle(entity.getTitle());
        List<Article_ImagesEntity> target = entity.getArticle_images();
        if (target.size() > 0){
            dto.setImage_url(target.get(0).getImage_url());
        }
        return dto;
    }

}
