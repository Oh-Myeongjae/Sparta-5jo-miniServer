package com.sparta.sp5miniserver.service;

import com.sparta.sp5miniserver.dto.response.ResponseDto;
import com.sparta.sp5miniserver.entity.*;
import com.sparta.sp5miniserver.repository.CommentLikeRepository;
import com.sparta.sp5miniserver.repository.PostLikeRepository;
import com.sparta.sp5miniserver.repository.PostRepository;
import com.sparta.sp5miniserver.utils.jwt.TokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LikeService {

    private final PostService postService;
    private final CommentService commentService;

    private final PostRepository postRepository;
    private final PostLikeRepository postLikeRepository;
    private final CommentLikeRepository commentLikeRepository;
    private final TokenProvider tokenProvider;

    @Transactional
    public ResponseDto<?> likePost(Long id, HttpServletRequest request) {
        Member member = validateMember(request);
        if (null == member) {
            return ResponseDto.fail("INVALID_TOKEN", "refresh token is invalid");
        }

        Post post = postService.isPresentPost(id);
        if (null == post) {
            return ResponseDto.fail("NOT_FOUND", "post id is not exist");
        }

        PostLike postLike = isPresentPostLike(member, post);

        if (null == postLike) {
            postLikeRepository.save(
                    PostLike.builder()
                            .member(member)
                            .post(post)
                            .build()
            );
            return ResponseDto.success("like success");
        } else {
            postLikeRepository.delete(postLike);
            return ResponseDto.success("cancel like success");
        }
    }//end of likePost

    @Transactional
    public ResponseDto<?> likeComment(Long id, HttpServletRequest request) {
        //토큰이 유효한지 검사
        Member member = validateMember(request);
        if (null == member) {
            return ResponseDto.fail("INVALID_TOKEN", "refresh token is invalid");
        }

        //DB에 해당 id의 코멘트가 있는지 검사
        Comment comment = commentService.isPresentComment(id);
        if (null == comment) {
            return ResponseDto.fail("NOT_FOUND", "comment id is not exist");
        }

        //이미 좋아요를 눌렀는지 검사한 후, 없다면 db에 저장. 있다면 DE에서 삭제
        CommentLike commentLike = isPresentCommentLike(member, comment);
        if (null == commentLike) {
            commentLikeRepository.save(
                    CommentLike.builder()
                            .member(member)
                            .comment(comment)
                            .build()
            );
            return ResponseDto.success("like success");
        } else {
            commentLikeRepository.delete(commentLike);  //삭제
            return ResponseDto.success("cancel like success");
        }
    }


    @Transactional(readOnly = true)
    public PostLike isPresentPostLike(Member member, Post post) {
        Optional<PostLike> optionalPostLike = postLikeRepository.findByMemberAndPost(member, post);
        return optionalPostLike.orElse(null);
    }

    @Transactional(readOnly = true)
    public Post isPresentPost(Long id) {
        Optional<Post> optionalPost = postRepository.findById(id);
        return optionalPost.orElse(null);
    }

    //유효성 검사
    @Transactional
    public Member validateMember(HttpServletRequest request) {
        if (!tokenProvider.validateToken(request.getHeader("Refresh-Token"))) {
            return null;
        }
        return tokenProvider.getMemberFromAuthentication();
    }

    //해당 코멘트에 멤버의 Like가 이미 존재하는지 검사.
    @Transactional(readOnly = true)
    public CommentLike isPresentCommentLike(Member member, Comment comment) {
        Optional<CommentLike> optionalCommentLike = commentLikeRepository.findByMemberAndComment(member, comment);
        return optionalCommentLike.orElse(null);
    }
}//end of LikeService