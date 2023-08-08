package com.example.project_2_yangyechan.service;

import com.example.project_2_yangyechan.dto.*;
import com.example.project_2_yangyechan.entity.*;
import com.example.project_2_yangyechan.repository.*;
import jakarta.validation.constraints.Null;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ArticleService {
    private final ArticleRepository articleRepository;
    private final Article_ImagesRepository articleImagesRepository;
    private final UserRepository userRepository;
    private final JpaUserDetailsManager manager;
    private final Like_ArticleRepository likeArticleRepository;
    private final Users_FollowsRepository usersFollowsRepository;

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
            // articleImage/유저이름_article_id/filename.png

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

    // Page
    public Page<RespondArticlePageDto> readArticlePaged(Integer pageNumber, Integer pageSize, RequestArticlePageDto dto) {
        Optional<UserEntity> optionalUser = userRepository.findByUsername(dto.getUsername());
        UserEntity user = optionalUser.get();
        if (optionalUser.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        // PagingAndSortingRepository 메소드에 전달하는 용도
        // 조회하고 싶은 페이지의 정보를 담는 객체
        // 20개씩 데이터를 나눌때 0번 페이지를 달라고 요청하는 Pageable
        Pageable pageable = PageRequest.of(
                pageNumber, pageSize, Sort.by("id").ascending());
        Page<ArticleEntity> articleEntityPage
                = articleRepository.findAllByUser(user, pageable);
        // map: 전달받은 함수를 각 원소에 인자로 전달한 결과를
        // 다시 모아서 Stream으로
        // Page.map: 전달받은 함수를 각 원소에 인자로 전달한 결과를
        // 다시 모아서 Page로
        Page<RespondArticlePageDto> articleDtoPage
                = articleEntityPage.map(RespondArticlePageDto::fromEntity);
        return articleDtoPage;
    }

    // 피드 단독 조회
    public RespondArticle readOnlyArticle(Long article_id, Authentication authentication) {
        String username = authentication.getName();
        Optional<UserEntity> optionalUser = userRepository.findByUsername(username);

        Optional<ArticleEntity> optionalEntity
                = articleRepository.findById(article_id);

        if (optionalUser.isPresent() && optionalEntity.isPresent()) {
            return RespondArticle.fromEntity(optionalEntity.get());
        }else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
    }

    // DELETE Image 피드 이미지 삭제
    public void removeArticleImage(Long article_id, Long image_id, Authentication authentication) {
        String username = authentication.getName();
        Optional<ArticleEntity> optionalArticle = articleRepository.findById(article_id);

        // 사용자 검증
        if (optionalArticle.get().getUser().getUsername().equals(username)) {
            List<Article_ImagesEntity> targets = optionalArticle.get().getArticle_images();

            // 삭제하고자하는 ArticleImage가 맞는지 검증
            for (Article_ImagesEntity target : targets) {
                if (target.getId().equals(image_id)) {
                    // 서버에 저장되어있는 사진 삭제
                    Optional<Article_ImagesEntity> optionalArticleImages = articleImagesRepository.findById(image_id);
                    String[] fileNameSplit = optionalArticleImages.get().getImage_url().split("/");
                    String fileType = fileNameSplit[fileNameSplit.length-1];
                    String fileName = String.format("articleImage/%s_%d/%s", username, article_id, fileType);
                    File file = new File(fileName);
                    file.delete();
                    articleImagesRepository.deleteById(image_id);
                }
            }
        }else {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }
    }

    // 피드 삭제 /사용자 입장에서만 삭제, 서버 DB에는 남아있음
    public void deleteArticleServie(Long article_id, Authentication authentication) {
        String username = authentication.getName();
        Optional<ArticleEntity> optionalArticle = articleRepository.findById(article_id);

        // 사용자 검증
        if (optionalArticle.get().getUser().getUsername().equals(username)) {
            // 현재 시간을 저장
            LocalDateTime currentTime = LocalDateTime.now();

            ArticleEntity article = optionalArticle.get();
            article.setDeleted_at(currentTime);
            articleRepository.save(article);
        }else {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }
    }

    // 좋아요 기능 /자신은 좋아요를 할 수 없음, 이미 좋아요 두번 누르면 좋아요는 취소
    public ResponseDto likeArticleServie (Long article_id, Authentication authentication) {
        // Article의 Deleted_at 필드가 null이 아니라면 반환하지 않음
        Optional<ArticleEntity> optionalArticle = articleRepository.findById(article_id);
        if (optionalArticle.get().getDeleted_at() != null)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);

        // 사용자 정보 회수
        String username = authentication.getName();
        Optional<UserEntity> user = userRepository.findByUsername(username);

        ResponseDto response = new ResponseDto();

        // 사용자 검증
        if (user.isPresent() && !optionalArticle.get().getUser().getUsername().equals(username)) {
            Optional<Like_ArticleEntity> likeArticle = likeArticleRepository.findByUserAndArticle(user.get(), optionalArticle.get());
            if (!likeArticle.isPresent()) {
                // 좋아요 새로 생성
                Like_ArticleEntity likeArticleEntity = new Like_ArticleEntity();
                likeArticleEntity.setUser(user.get());
                likeArticleEntity.setArticle(optionalArticle.get());
                likeArticleRepository.save(likeArticleEntity);
                response.setMessage("좋아요");
                return response;
            } else {
                // 좋아요 취소
                likeArticleRepository.deleteById(likeArticle.get().getId());
                response.setMessage("좋아요 취소");
                return response;
            }
        }else {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }
    }

    // Page
    public Page<RespondArticlePageDto> readArticleFollowersPaged(Integer pageNumber, Integer pageSize, Authentication authentication) {
        String username = authentication.getName();
        Optional<UserEntity> optionalUser = userRepository.findByUsername(username);

        if (optionalUser.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        // PagingAndSortingRepository 메소드에 전달하는 용도
        // 조회하고 싶은 페이지의 정보를 담는 객체
        // 20개씩 데이터를 나눌때 0번 페이지를 달라고 요청하는 Pageable
        UserEntity user = optionalUser.get();
        // 해당 유저가 팔로우한 팔로워들의 목록을 가져옴
        List<UserEntity> followers = usersFollowsRepository.findAllByFollower(user);
        // 각 팔로워들의 Article을 조회하고 페이지로 나누어 역순으로 정렬
        for (UserEntity follower : followers) {

        }
        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by("id").descending());
        Page<ArticleEntity> articleEntityPage
                = articleRepository.findAllByUser(user, pageable);
        Page<RespondArticlePageDto> articleDtoPage
                = articleEntityPage.map(RespondArticlePageDto::fromEntity);
        return articleDtoPage;
    }


}
