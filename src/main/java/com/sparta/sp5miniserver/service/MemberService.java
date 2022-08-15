package com.sparta.sp5miniserver.service;


import com.sparta.sp5miniserver.dto.SignUpRequest;
import com.sparta.sp5miniserver.entity.Member;
import com.sparta.sp5miniserver.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.constraints.Null;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    //DI

    @Transactional
    public Member signUp(SignUpRequest request){
        //유효성 검사
        request.validate();

        //중복된 id가 있는지 레포지토리를 검사
        //System.out.println("request.getMemberId() ================= "+ request.getMemberId());
        if(memberRepository.findByMemberId(request.getMemberId()).orElse(null)!=null) {
            throw new IllegalArgumentException("이미 존재하는 id 입니다");
        }

        Member member = new Member();
        member.setMemberId(request.getMemberId());
        member.setNickname(request.getNickname());
        member.setPassword(request.getPassword());

        //db에 저장하고 반환
        return memberRepository.save(member);  // response 수정해야됨...
    }
}
