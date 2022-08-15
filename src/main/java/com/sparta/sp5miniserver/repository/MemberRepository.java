package com.sparta.sp5miniserver.repository;


import com.sparta.sp5miniserver.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Integer> {
    Optional<Member> findByMemberId(String memberId);
}
