package com.sparta.sp5miniserver.repository;

import java.util.Optional;
import com.sparta.sp5miniserver.Entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Integer> {
    Optional<Member> findByMemberId(String memberId);
}
