package com.sparta.sp5miniserver.repository;

import com.sparta.sp5miniserver.entity.Member;
import com.sparta.sp5miniserver.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
  Optional<RefreshToken> findByMember(Member member);
}
