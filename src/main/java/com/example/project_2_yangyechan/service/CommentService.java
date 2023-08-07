package com.example.project_2_yangyechan.service;

import com.example.project_2_yangyechan.dto.CommentDto;
import com.example.project_2_yangyechan.entity.ArticleEntity;
import com.example.project_2_yangyechan.entity.CommentEntity;
import com.example.project_2_yangyechan.entity.UserEntity;
import com.example.project_2_yangyechan.repository.ArticleRepository;
import com.example.project_2_yangyechan.repository.CommentRepository;
import com.example.project_2_yangyechan.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final ArticleRepository articleRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    // CREATE
    public void createComment(CommentDto dto, Long article_id, Authentication authentication) {
        if (!articleRepository.existsById(article_id))
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);

        // Article의 Deleted_at 필드가 null이 아니라면 반환하지 않음
        Optional<ArticleEntity> optionalArticle = articleRepository.findById(article_id);
        if (optionalArticle.get().getDeleted_at() != null)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);

        // 사용자 정보 회수
        String username = authentication.getName();
        Optional<UserEntity> user = userRepository.findByUsername(username);

        if (user.isPresent()){
            // ArticleEntity 객체로 매핑된 article 필드에 대한 참조 설정
            Optional<ArticleEntity> article = articleRepository.findById(article_id);

            CommentEntity newItem = new CommentEntity();
            newItem.setArticle(article.get());
            newItem.setContent(dto.getContent());
            newItem.setUser(user.get());
            CommentDto.fromEntity(commentRepository.save(newItem));
        }else {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }
    }
}
