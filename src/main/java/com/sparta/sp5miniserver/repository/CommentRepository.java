package com.sparta.sp5miniserver.repository;

import com.sparta.sp5miniserver.entity.Comment;
import com.sparta.sp5miniserver.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByMember(Member member);

}
