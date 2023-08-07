package com.example.project_2_yangyechan.controller;

import com.example.project_2_yangyechan.dto.CommentDto;
import com.example.project_2_yangyechan.dto.ResponseDto;
import com.example.project_2_yangyechan.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/articles")
@RequiredArgsConstructor
public class CommentController {
    private final CommentService commentService;
    @PostMapping("/{id}/comments")
    public ResponseDto create(
            @RequestBody CommentDto commentDto,
            @PathVariable("id") Long article_id,
            Authentication authentication) {
        commentService.createComment(commentDto, article_id, authentication);
        ResponseDto response = new ResponseDto();
        response.setMessage("댓글이 등록되었습니다.");
        return response;
    }
}
