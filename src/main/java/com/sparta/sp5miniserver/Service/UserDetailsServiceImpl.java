package com.sparta.sp5miniserver.service;

import com.sparta.sp5miniserver.entity.Member;
import com.sparta.sp5miniserver.entity.UserDetailsImpl;
import com.sparta.sp5miniserver.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {
  private final MemberRepository memberRepository;

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
//    Optional<Member> member = memberRepository.findById(username);
//    return member
//        .map(UserDetailsImpl::new)
//        .orElseThrow(() -> new UsernameNotFoundException("nickname not found"));
    Member member = new Member("1","dhaudwo","asdf1234");
    return new UserDetailsImpl(member);
  }
}
