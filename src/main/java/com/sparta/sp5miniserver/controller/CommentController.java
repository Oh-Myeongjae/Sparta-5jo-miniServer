package com.sparta.sp5miniserver.controller;

import com.sparta.sp5miniserver.dto.request.CommentRequestDto;
import com.sparta.sp5miniserver.dto.response.ResponseDto;
import com.sparta.sp5miniserver.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;


@RequiredArgsConstructor // 생성자 주입
@RestController
@RequestMapping("/api")
public class CommentController {
    private final CommentService commentService;

    @PostMapping("/comment/{postId}")  //생성
    public ResponseDto<?> createComment(
            @PathVariable Long postId, @RequestBody CommentRequestDto commentRequestDto, HttpServletRequest request, @AuthenticationPrincipal UserDetails user){
        return commentService.createComment(postId, commentRequestDto, user.getUsername());
    }

    @PutMapping("/comment/{commentId}") // 수정
    public ResponseDto<?> updateComment(
            @PathVariable Long commentId,@RequestBody CommentRequestDto commentRequestDto, HttpServletRequest request, @AuthenticationPrincipal UserDetails user){
            return commentService.updateComment(commentId, commentRequestDto, user.getUsername());
    }


    @DeleteMapping("/comment/{commentId}") // 삭제
    public ResponseDto<?> deleteComment(@PathVariable Long commentId, HttpServletRequest request){
        return commentService.deleteComment(commentId);
    }

}
