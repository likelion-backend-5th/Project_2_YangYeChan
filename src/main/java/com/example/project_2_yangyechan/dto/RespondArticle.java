package com.example.project_2_yangyechan.dto;

import com.example.project_2_yangyechan.entity.ArticleEntity;
import com.example.project_2_yangyechan.entity.Article_ImagesEntity;
import com.example.project_2_yangyechan.entity.CommentEntity;
import com.example.project_2_yangyechan.entity.UserEntity;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class RespondArticle {
    private String username;

    private String title;

    private String content;

    private List<String> articleImagesUrl;

    private List<Comments> comments;

    private Integer likes;

    public static RespondArticle fromEntity(ArticleEntity entity) {
        RespondArticle dto = new RespondArticle();
        dto.setUsername(entity.getUser().getUsername());
        dto.setTitle(entity.getTitle());
        dto.setContent(entity.getContent());

        List<String> images = new ArrayList<>();
        for (Article_ImagesEntity image : entity.getArticle_images()) {
            images.add(image.getImage_url());
        }
        dto.setArticleImagesUrl(images);

        List<Comments> comments = new ArrayList<>();
        for (CommentEntity comment : entity.getComments()) {
            Comments targetComments = new Comments();
            targetComments.setUsername(comment.getUser().getUsername());
            targetComments.setContent(comment.getContent());
            comments.add(targetComments);
        }
        dto.setComments(comments);

        dto.setLikes(entity.getLike_articles().size());
        return dto;
    }
}
