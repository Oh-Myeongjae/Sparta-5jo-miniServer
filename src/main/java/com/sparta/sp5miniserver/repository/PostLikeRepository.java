package com.sparta.sp5miniserver.repository;

import com.sparta.sp5miniserver.entity.Member;
import com.sparta.sp5miniserver.entity.Post;
import com.sparta.sp5miniserver.entity.PostLike;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PostLikeRepository extends JpaRepository<PostLike, Long> {
    Optional<PostLike> findByMemberAndPost(Member member, Post post);
    List<PostLike> findAllByPost(Post post);
    List<PostLike> findAllByMember(Member member);
}
