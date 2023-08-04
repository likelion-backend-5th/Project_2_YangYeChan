package com.example.project_2_yangyechan.service;

import com.example.project_2_yangyechan.dto.ArticleDto;
import com.example.project_2_yangyechan.entity.ArticleEntity;
import com.example.project_2_yangyechan.entity.Article_ImagesEntity;
import com.example.project_2_yangyechan.entity.UserEntity;
import com.example.project_2_yangyechan.repository.ArticleRepository;
import com.example.project_2_yangyechan.repository.Article_ImagesRepository;
import com.example.project_2_yangyechan.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ArticleService {
    private final ArticleRepository articleRepository;
    private final Article_ImagesRepository articleImagesRepository;
    private final UserRepository userRepository;
    private final JpaUserDetailsManager manager;

    // CREATE
    public void createArticle(ArticleDto dto, Authentication authentication) {
        String username = authentication.getName();
        // 사용자 정보 회수
        Optional<UserEntity> user = userRepository.findByUsername(username);

        if (user.isPresent()){
            ArticleEntity newArticle = new ArticleEntity();

            newArticle.setUser(user.get());
            newArticle.setTitle(dto.getTitle());
            newArticle.setContent(dto.getContent());
            newArticle.setDraft(dto.getDraft());
            newArticle.setDeleted_at(dto.getDeleted_at());

            articleRepository.save(newArticle);
        }
        else {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }
    }

    // Post Image
    public void createArticleImage(Long article_id, MultipartFile articleImage, Authentication authentication) {

        String username = authentication.getName();

        // 해당하는 피드 정보
        Optional<ArticleEntity> optionalArticle
                = articleRepository.findById(article_id);

        //Optional<Article_ImagesEntity> optionalArticleImages
                //= articleImagesRepository.findByArticle(optionalArticle.get());

        if (optionalArticle.isPresent()
                && optionalArticle.get().getUser().getUsername().equals(username)
                && optionalArticle.get().getId().equals(article_id)) {
            // articleImage/filename.png
            // articleImage/<업로드 시각>.png
            // 2. 파일을 어디에 업로드 할건지
            // articleImage/{userId}/profile.{파일 확장자}

            // 2-1. 폴더만 만드는 과정
            //
            String profileDir = String.format("articleImage/%s_%d/", username, article_id);
            try {
                Files.createDirectories(Path.of(profileDir));
            } catch (IOException e) {
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
            }

            // 2-2. 확장자를 포함한 이미지 이름 만들기 (image_{cnt}.{확장자})
            String originalFilename = articleImage.getOriginalFilename();
            // queue.png => fileNameSplit = {"queue", "png"}
            String[] fileNameSplit = originalFilename.split("\\.");
            String extension = fileNameSplit[fileNameSplit.length - 1];
            Integer cnt = 1;
            for (Article_ImagesEntity target : articleImagesRepository.findAllByArticle(optionalArticle.get())) {
                if (target.getArticle().getId().equals(article_id)) {
                    ++cnt;
                }
            }
            System.out.println(cnt);
            String profileFilename = "image_" + String.valueOf(cnt) + "." + extension;

            // 2-3. 폴더와 파일 경로를 포함한 이름 만들기
            String profilePath = profileDir + profileFilename;


            // 3. MultipartFile 을 저장하기
            try {
                articleImage.transferTo(Path.of(profilePath));
            } catch (IOException e) {

                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
            }

            // 4. UserEntity 업데이트 (정적 프로필 이미지를 회수할 수 있는 URL)
            // http://localhost:8080/static/1/profile.png


            Article_ImagesEntity targetEntity = new Article_ImagesEntity();
            targetEntity.setArticle(optionalArticle.get());
            targetEntity.setImage_url(String.format("/static/%d/%s", article_id, profileFilename));
            articleImagesRepository.save(targetEntity);

        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
    }
}
