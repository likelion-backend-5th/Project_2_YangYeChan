package com.example.project_2_yangyechan.controller;

import com.example.project_2_yangyechan.dto.*;
import com.example.project_2_yangyechan.entity.ArticleEntity;
import com.example.project_2_yangyechan.repository.ArticleRepository;
import com.example.project_2_yangyechan.service.ArticleService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@RestController
@RequestMapping("/articles")
@RequiredArgsConstructor
public class ArticleController {
    private final ArticleService articleService;
    private final ArticleRepository articleRepository;

    // POST /articles
    // 새 아이템 생성
    @PostMapping
    public ResponseDto create(@RequestBody ArticleDto articleDto, Authentication authentication) {
        articleService.createArticle(articleDto, authentication);
        ResponseDto response = new ResponseDto();
        response.setMessage("등록이 완료되었습니다.");
        return response;
    }

    // POST /articles/{id}/image
    // 피드 이미지
    @PostMapping(
            value = "/{id}/image",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseDto postImage(
            @PathVariable("id") Long article_id,
            @RequestParam("image") MultipartFile articleImage,
            Authentication authentication
    ) {
        articleService.createArticleImage(article_id, articleImage, authentication);
        ResponseDto response = new ResponseDto();
        response.setMessage("이미지가 등록되었습니다.");
        return response;
    }

    // User Article Page Read
    @GetMapping("/pages")
    public Page<RespondArticlePageDto> readAll(
            @RequestBody RequestArticlePageDto dto
    ) {
        Integer page = 0;
        Integer limit = 25;
        return articleService.readArticlePaged(page, limit, dto);
    }

    // GET
    // Article 단독 조회
    @GetMapping("/{id}/page")
    public RespondArticle readArticleByArticleId(
            @PathVariable("id") Long article_id,
            Authentication authentication
    ) {
        return articleService.readOnlyArticle(article_id, authentication);
    }

    // DELETE /articles/{id}/image/update
    @DeleteMapping("/{id}/image/{image_id}/update")
    public ResponseDto updateArticleImage(
            @PathVariable("id") Long article_id,
            @PathVariable("image_id") Long image_id,
            @RequestBody RequestArticleDto request,
            Authentication authentication
    ) {
        if (request.getArticle_request().equals("이미지 삭제")) {
            articleService.removeArticleImage(article_id, image_id, authentication);
            ResponseDto response = new ResponseDto();
            response.setMessage("이미지가 삭제되었습니다.");
            return response;
        }else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
    }

    // PUT 피드 삭제
    @PutMapping("/{id}/delete")
    public ResponseDto deleteArticle(
            @PathVariable("id") Long article_id,
            @RequestBody RequestArticleDto request,
            Authentication authentication
    ) {
        if (request.getArticle_request().equals("피드 삭제")) {
            articleService.deleteArticleServie(article_id, authentication);
            ResponseDto response = new ResponseDto();
            response.setMessage("피드가 삭제되었습니다.");
            return response;
        }else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
    }

    // POST /좋아요 구현
    @PostMapping("/{id}/like")
    public ResponseDto likeArticle(
            @PathVariable("id") Long article_id,
            @RequestBody RequestArticleDto request,
            Authentication authentication
    ) {
        if (request.getArticle_request().equals("좋아요")) {
            return articleService.likeArticleServie(article_id, authentication);
        }else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
    }

    // GET 팔로우한 모든 사용자의 피드목록 조회
    @GetMapping("/home")
    public Page<RespondArticlePageDto> readAllFollower(
            Authentication authentication
    ) {
        Integer page = 0;
        Integer limit = 25;
        return articleService.readArticleFollowersPaged(page, limit, authentication);
    }
}
