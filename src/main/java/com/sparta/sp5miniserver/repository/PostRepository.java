package com.sparta.sp5miniserver.repository;

import com.sparta.sp5miniserver.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Long> {
}
