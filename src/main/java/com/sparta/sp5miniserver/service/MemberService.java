package com.sparta.sp5miniserver.service;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sparta.sp5miniserver.dto.SignUpRequest;
import com.sparta.sp5miniserver.dto.response.ResponseDto;
import com.sparta.sp5miniserver.entity.KakaoMember;
import com.sparta.sp5miniserver.entity.Member;
import com.sparta.sp5miniserver.repository.KakaoMemberRepository;
import com.sparta.sp5miniserver.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import javax.validation.constraints.Null;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final KakaoMemberRepository kakaoMemberRepository;
    //DI

    @Transactional
    public ResponseDto<?> signUp(SignUpRequest request){
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
        memberRepository.save(member);
        //db에 저장하고 반환
        return ResponseDto.success(member);// response 수정해야됨...
    }
    @Transactional
    public ResponseDto<?> kakaoLogin(String code) throws JsonProcessingException {

        // 1. "인가 코드"로 "액세스 토큰" 요청
// HTTP Header 생성
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

// HTTP Body 생성
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "authorization_code");
        body.add("client_id", "2e0492dd18239e1ace5c0d238be87e92");
        body.add("redirect_uri", "http://localhost:8080/api/kakao/callback");
        body.add("code", code);  //인가코드



// HTTP 요청 보내기
        HttpEntity<MultiValueMap<String, String>> kakaoTokenRequest =
                new HttpEntity<>(body, headers);
        RestTemplate rt = new RestTemplate();   // RestTemplate 많이 사용함..
        ResponseEntity<String> response = rt.exchange(
                "https://kauth.kakao.com/oauth/token",
                HttpMethod.POST,
                kakaoTokenRequest,  //헤더와 바디를 같이 넣음..
                String.class
        );

// HTTP 응답 (JSON) -> 액세스 토큰 파싱
        String responseBody = response.getBody();
        ObjectMapper objectMapper = new ObjectMapper(); //json형태를 java에서 사용하기 위해서..
        JsonNode jsonNode = objectMapper.readTree(responseBody);
        String accessToken = jsonNode.get("access_token").asText();
        //여기서 빼낸 엑세스 토큰으로 카카오서버에 api호출을 할거임.. 여기까지가 클라이언트..


        // 2. 토큰으로 카카오 API 호출
// HTTP Header 생성
        headers.add("Authorization", "Bearer " + accessToken);
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

// HTTP 요청 보내기
        HttpEntity<MultiValueMap<String, String>> kakaoUserInfoRequest = new HttpEntity<>(headers);
        response = rt.exchange(
                "https://kapi.kakao.com/v2/user/me",
                HttpMethod.POST,
                kakaoUserInfoRequest,
                String.class
        );

        responseBody = response.getBody();
        jsonNode = objectMapper.readTree(responseBody);
        Long id = jsonNode.get("id").asLong();
        String nickname = jsonNode.get("properties")
                .get("nickname").asText();
        String email = jsonNode.get("kakao_account")
                .get("email").asText();
        System.out.println(responseBody);
        System.out.println("카카오 사용자 정보: " + id + ", " + nickname + ", " + email);

        // 만약 넣는다면 멤버아이디는 이메일, 닉네임은 닉네임( 여기서 추가적으로 회원 가입및 로그인처리를 진행함..
        KakaoMember kakaoMember = KakaoMember.builder()
                .memberId(email)
                .nickname(nickname)
                .password("임의의 값을 인코딩")
                .build();

        kakaoMemberRepository.save(kakaoMember);

        return ResponseDto.success(kakaoMember);
    }
}
