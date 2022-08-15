package com.sparta.sp5miniserver.service;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sparta.sp5miniserver.dto.SignUpRequest;
import com.sparta.sp5miniserver.dto.response.MemberResponseDto;
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

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.Null;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.UUID;

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
        return ResponseDto.success(
                    MemberResponseDto.builder()
                            .id(member.getId())
                            .memberId(member.getMemberId())
                            .nickName(member.getNickname())
                            .password(member.getPassword())
                            .build()
        );
    }
    @Transactional
    public ResponseDto<?> kakaoLogin(String code) throws JsonProcessingException {

        // 1. "인가 코드"로 "액세스 토큰" 요청
// HTTP Header 생성    https://developers.kakao.com/docs/latest/ko/kakaologin/rest-api#request-token-request
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

// HTTP Body 생성
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "authorization_code");
        body.add("client_id", "2e0492dd18239e1ace5c0d238be87e92");   // REST API KEY
        body.add("redirect_uri", "http://localhost:8080/api/kakao/callback"); // 인가 코드가 리다이렉트된 URI
        body.add("code", code);  //인가코드



// HTTP 요청 보내기
        HttpEntity<MultiValueMap<String, String>> kakaoTokenRequest =
                new HttpEntity<>(body, headers);
        RestTemplate rt = new RestTemplate();   // RestTemplate 많이 사용함..
        ResponseEntity<String> response = rt.exchange(     // 결과 값은 바디에 JSON형태로 들어온다(디벨로프사이트에서 확인가능)
                "https://kauth.kakao.com/oauth/token",
                HttpMethod.POST,
                kakaoTokenRequest,  //헤더와 바디를 합침
                String.class
        );

// HTTP 응답 (JSON) -> 바디 부분에 잇는 액세스 토큰을 파싱
        String responseBody = response.getBody();
        ObjectMapper objectMapper = new ObjectMapper(); //json형태를 java에서 사용하기 위해서..
        JsonNode jsonNode = objectMapper.readTree(responseBody);
        String accessToken = jsonNode.get("access_token").asText();
        //여기서 빼낸 엑세스 토큰으로 카카오서버에 api호출을 할거임..


        // 2. 토큰으로 카카오 API 호출  (https://developers.kakao.com/docs/latest/ko/kakaologin/rest-api#req-user-info)
// HTTP Header 생성
        headers.add("Authorization", "Bearer " + accessToken);
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

// HTTP 요청 보내기
        HttpEntity<MultiValueMap<String, String>> kakaoUserInfoRequest = new HttpEntity<>(headers);
        response = rt.exchange(  //JSON형태의 바디에 온다.
                "https://kapi.kakao.com/v2/user/me",
                HttpMethod.POST,
                kakaoUserInfoRequest,
                String.class
        );

        responseBody = response.getBody();
        jsonNode = objectMapper.readTree(responseBody);
        System.out.println(jsonNode);

        Long id = jsonNode.get("id").asLong();
        String nickname = jsonNode.get("properties")
                .get("nickname").asText();
        String email = jsonNode.get("kakao_account")
                .get("email").asText();


        System.out.println("카카오 사용자 정보: " + id + ", " + nickname + ", " + email);

        // 만약 넣는다면 멤버아이디는 이메일, 닉네임은 닉네임( 여기서 추가적으로 회원 가입및 로그인처리를 진행함..
        KakaoMember kakaoMember = KakaoMember.builder()
                .memberId(email)
                .nickname(nickname)
                .password(UUID.randomUUID().toString())  // 임의의 값을 넣음, 암호화해야됨.
                .build();

        kakaoMemberRepository.save(kakaoMember); // 회원가입은 됬으나 로그인 기능도 추가해야함..

        return ResponseDto.success(
                MemberResponseDto.builder()
                        .id(kakaoMember.getId())
                        .memberId(kakaoMember.getMemberId())
                        .nickName(kakaoMember.getNickname())
                        .password(kakaoMember.getPassword())
                        .build()
        );
    }

    public ResponseDto<?> kakaoLogout(HttpServletRequest request) {
        String reqURL = "https://kapi.kakao.com/v1/user/logout";
        String access_Token = request.getHeader("access_token");

        try {
            URL url = new URL(reqURL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Authorization", "Bearer " + access_Token);

            int responseCode = conn.getResponseCode();
            System.out.println("responseCode : " + responseCode);

            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));

            String result = "";
            String line = "";

            while ((line = br.readLine()) != null) {
                result += line;
            }
            System.out.println(result);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return ResponseDto.success("좋아");
    }
}
