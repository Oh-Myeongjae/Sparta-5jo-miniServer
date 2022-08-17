package com.sparta.sp5miniserver.repository;

import com.sparta.sp5miniserver.entity.Comment;
import com.sparta.sp5miniserver.entity.CommentLike;
import com.sparta.sp5miniserver.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CommentLikeRepository extends JpaRepository<CommentLike, Long> {
    Optional<CommentLike> findByMemberAndComment(Member member, Comment comment);
    List<CommentLike> findAllByComment(Comment comment);
    List<CommentLike> findAllByMember(Member member);
}
