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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import javax.validation.constraints.Null;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final TokenProvider tokenProvider;

    private  final PasswordEncoder passwordEncoder;

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
        member.setPassword(passwordEncoder.encode(request.getPassword()));

        //db에 저장하고 반환
        return memberRepository.save(member);
    }

    @Transactional
    public ResponseDto<?> login(LoginRequestDto requestDto, HttpServletResponse response) {
        Member member = isPresentMember(requestDto.getId());
        if (null == member) {
            return ResponseDto.fail("MEMBER_NOT_FOUND",
                    "잘못된 아이디 입니다.");
        }

        if (!member.validatePassword(passwordEncoder,requestDto.getPassword())){
            return ResponseDto.fail("PASSWORDS_NOT_MATCHED",
                    "비밀번호를 잘못 입력했습니다.");
        }

        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(requestDto.getId(), requestDto.getPassword());
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        TokenDto tokenDto = tokenProvider.generateTokenDto(authentication);
        tokenToHeaders(tokenDto, response);

        ResponseDto responseDto = ResponseDto.success("sucess");
        return responseDto;
    }

    public ResponseDto<?> logout(HttpServletRequest request) {
        if (!tokenProvider.validateToken(request.getHeader("Refresh-Token"))) {
            return ResponseDto.fail("INVALID_TOKEN", "refresh token is invalid");
        }
        Member member = tokenProvider.getMemberFromAuthentication();
        if (null == member) {
            return ResponseDto.fail("MEMBER_NOT_FOUND",
                    "member not found");
        }

        return tokenProvider.deleteRefreshToken(member);
//        return ResponseDto.success("sucess");
    }

    @Transactional(readOnly = true)
    public Member isPresentMember(String MemberId) {
        Optional<Member> optionalMember = memberRepository.findByMemberId(MemberId);
        return optionalMember.orElse(null);
    }

    public void tokenToHeaders(TokenDto tokenDto, HttpServletResponse response) {
        response.addHeader("Authorization", "Bearer " + tokenDto.getAccessToken());
        response.addHeader("Refresh-Token", tokenDto.getRefreshToken());
        response.addHeader("Access-Token-Expire-Time", tokenDto.getAccessTokenExpiresIn().toString());
    }
}

