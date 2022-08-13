package com.sparta.sp5miniserver.service;

import com.sparta.sp5miniserver.dto.SignUpRequest;
import com.sparta.sp5miniserver.dto.request.LoginRequestDto;
import com.sparta.sp5miniserver.dto.response.ResponseDto;
import com.sparta.sp5miniserver.entity.Member;
import com.sparta.sp5miniserver.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletResponse;

@Service
public class MemberService {

    private final MemberRepository memberRepository;

    //DI
    @Autowired
    public MemberService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    @Transactional
    public Member signUp(SignUpRequest request) {
        //유효성 검사
        request.validate();

        //중복된 id가 있는지 레포지토리를 검사
        if (memberRepository.existsById(request.getMemberId())) {
            throw new IllegalArgumentException("이미 존재하는 id 입니다");
        }

        Member member = new Member();
        member.setId(request.getMemberId());
        member.setNickname(request.getNickname());
        member.setPassword(request.getPassword());

        //db에 저장하고 반환
        return memberRepository.save(member);
    }

    @Transactional
    public ResponseDto<?> login(LoginRequestDto requestDto, HttpServletResponse response) {
//        Member member = new Member();
        String member = requestDto.getId();
        if (null == member) {
            return ResponseDto.fail("MEMBER_NOT_FOUND",
                    "member not found");
        }

//        UsernamePasswordAuthenticationToken authenticationToken =
//                new UsernamePasswordAuthenticationToken(requestDto.getNickname(), requestDto.getPassword());
//        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
//
//        TokenDto tokenDto = tokenProvider.generateTokenDto(authentication);
//        tokenToHeaders(tokenDto, response);
//
        return ResponseDto.success("sucess");
    }
}
