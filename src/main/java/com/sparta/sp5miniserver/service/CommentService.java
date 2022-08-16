package com.sparta.sp5miniserver.service;

import com.sparta.sp5miniserver.dto.request.CommentRequestDto;
import com.sparta.sp5miniserver.dto.response.CommentResponseDto;
import com.sparta.sp5miniserver.dto.response.ResponseDto;
import com.sparta.sp5miniserver.entity.Comment;
import com.sparta.sp5miniserver.entity.Member;
import com.sparta.sp5miniserver.entity.Post;
import com.sparta.sp5miniserver.repository.CommentRepository;
import com.sparta.sp5miniserver.repository.MemberRepository;
import com.sparta.sp5miniserver.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final MemberRepository memberRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;

    public ResponseDto<?> createComment(Long postId, CommentRequestDto commentRequestDto, String user) {
        Member member = memberRepository.findByMemberId(user).orElse(null);
        Post post = postRepository.findById(postId).orElse(null);
        if(member == null || post == null){return ResponseDto.fail("BAD_REQUEST","잘못된 요청입니다.");}
        Comment comment = Comment.builder()
                .member(member)
                .post(post)
                .content(commentRequestDto.getContent())
                .build();
        commentRepository.save(comment);

        CommentResponseDto commentResponseDto = CommentResponseDto.builder()
                .id(comment.getId())
                .content(comment.getContent())
                .memberId(comment.getMember().getId())
                .postId(comment.getPost().getId())
                .build();
        return ResponseDto.success(commentResponseDto);
    }

    public ResponseDto<?> updateComment(Long commentId, CommentRequestDto commentRequestDto, String user) {

        Member member = memberRepository.findByMemberId(user).orElse(null);
        Comment comment = commentRepository.findById(commentId).orElse(null);

        if(member == null || comment == null){return ResponseDto.fail("BAD_REQUEST","잘못된 요청입니다.");}

        comment.update(commentRequestDto);

        CommentResponseDto commentResponseDto = CommentResponseDto.builder()
                .id(comment.getId())
                .content(comment.getContent())
                .memberId(comment.getMember().getId())
                .postId(comment.getPost().getId())
                .build();

        return ResponseDto.success(commentResponseDto);

    }

    public ResponseDto<?> deleteComment(Long commentId) {
        if(commentRepository.findById(commentId).isEmpty()){return ResponseDto.fail("BAD_REQUEST","잘못된 요청입니다.");}
        commentRepository.deleteById(commentId);
        return ResponseDto.success("성공");

    }
}
