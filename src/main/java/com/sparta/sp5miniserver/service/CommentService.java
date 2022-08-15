//package com.sparta.sp5miniserver.service;
//
//import com.sparta.sp5miniserver.dto.request.CommentRequestDto;
//import com.sparta.sp5miniserver.dto.response.ResponseDto;
//import com.sparta.sp5miniserver.entity.Comment;
//import com.sparta.sp5miniserver.entity.Member;
//import com.sparta.sp5miniserver.repository.CommentRepository;
//import com.sparta.sp5miniserver.repository.MemberRepository;
//import com.sparta.sp5miniserver.repository.PostRepository;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Service;
//
//import java.util.Optional;
//
//@Service
//@RequiredArgsConstructor
//public class CommentService {
//
//    private final MemberRepository memberRepository;
//    private final PostRepository postRepository;
//    private final CommentRepository commentRepository;
//
//
//    public ResponseDto<?> createComment(Long postId, CommentRequestDto commentRequestDto) {
//
//    ///// 멤버를 어떻게 불러올 것인가에 대한 문제 ! 리퀘스트에 토큰을 통해 멤버 정보를 불러온다!!
//        // 임시로 멤버 를 불러온다 그냥
//
//
//
//        Comment comment = Comment.builder()
//                .member()
//                .post()
//                .content()
//                .build();
//        commentRepository.save(comment);
//
//
//
//        return ResponseDto.success(comment);
//
//    }
//
//    public ResponseDto<?> updateComment(Long commentId, CommentRequestDto commentRequestDto) {
//
//        Optional comment = commentRepository.findById(commentId);
//
//        Comment comment = Comment.builder()
//                .member()
//                .post()
//                .content()
//                .build();
//        commentRepository.save(comment);
//
//        return ResponseDto.success(comment)
//    }
//
//    public ResponseDto<?> deleteComment(Long commentId) {
//
//
//        commentRepository.deleteById(commentId);
//        return ResponseDto.success("성공");
//
//    }
//}
