package com.sparta.sp5miniserver.service;

import com.sparta.sp5miniserver.dto.SignUpRequest;
import com.sparta.sp5miniserver.dto.request.LoginRequestDto;
import com.sparta.sp5miniserver.dto.request.TokenDto;
import com.sparta.sp5miniserver.dto.response.ResponseDto;
import com.sparta.sp5miniserver.entity.Member;
import com.sparta.sp5miniserver.repository.MemberRepository;
import com.sparta.sp5miniserver.utils.jwt.TokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletResponse;

@RequiredArgsConstructor
@Service
public class MemberService {
    private final MemberRepository memberRepository;

    private final AuthenticationManagerBuilder authenticationManagerBuilder;

    private final TokenProvider tokenProvider;

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
        System.out.println("========================================1");
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(requestDto.getId(), requestDto.getPassword());
        System.out.println("========================================2");
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        System.out.println("========================================3");
        TokenDto tokenDto = tokenProvider.generateTokenDto(authentication);
        System.out.println("========================================4");
        tokenToHeaders(tokenDto, response);
        System.out.println("========================================5");
//
        ResponseDto responseDto = ResponseDto.success("sucess");
        System.out.println("responseDto = " + responseDto);
        return responseDto;
    }

    public void tokenToHeaders(TokenDto tokenDto, HttpServletResponse response) {
        response.addHeader("Access-Token", "Bearer " + tokenDto.getAccessToken());
//        response.addHeader("Refresh-Token", "Bearer " + tokenDto.getRefreshToken());
        response.addHeader("Access-Token-Expire-Time", tokenDto.getAccessTokenExpiresIn().toString());
    }
}

