package com.example.project_2_yangyechan.controller;

import com.example.project_2_yangyechan.dto.ArticleDto;
import com.example.project_2_yangyechan.dto.ResponseDto;
import com.example.project_2_yangyechan.service.ArticleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/articles")
@RequiredArgsConstructor
public class ArticleController {
    private final ArticleService articleService;
    // POST /articles
    // 새 아이템 생성
    @PostMapping
    public ResponseDto create(@RequestBody ArticleDto itemDto, Authentication authentication) {
        articleService.createArticle(itemDto, authentication);
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
            @PathVariable("id") Long aricle_id,
            @RequestParam("image") MultipartFile avatarImage,
            Authentication authentication
    ) {
        articleService.createArticleImage(aricle_id, avatarImage, authentication);
        ResponseDto response = new ResponseDto();
        response.setMessage("이미지가 등록되었습니다.");
        return response;
    }
}
