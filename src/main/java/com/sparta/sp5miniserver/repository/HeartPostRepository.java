package com.sparta.sp5miniserver.repository;

import com.sparta.sp5miniserver.entity.HeartPost;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HeartPostRepository extends JpaRepository<HeartPost, Long> {
}
