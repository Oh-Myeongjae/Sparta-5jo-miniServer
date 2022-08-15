package com.sparta.sp5miniserver.controller;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.sparta.sp5miniserver.dto.SignUpRequest;
import com.sparta.sp5miniserver.dto.response.ResponseDto;
import com.sparta.sp5miniserver.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
public class MemberController {
    private final MemberService memberService;

    @Autowired
    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }


    @PostMapping("/api/signup")
    public ResponseDto<?> signUp(@RequestBody SignUpRequest request){

        return memberService.signUp(request);
    }


    ///소셜 로그인

    @GetMapping("/api/kakao/callback")
    public ResponseDto<?> kakaoLogin(@RequestParam String code) throws JsonProcessingException {
       return memberService.kakaoLogin(code);  // 받은 토큰이 이쪽으로 왔다면!!
        // Kakao Developer에서 Redirect URL을 프론트 서버쪽으로 연결한 후  백엔드 API쪽  보내줘야됨.
        // code 가 받은 인가코드임.

//        return "redirect:/"; // template에있는 '/' 쪽으로 이동
    }

    @GetMapping("/api/kakao/logout")
    public ResponseDto<?> kakaoLogout(HttpServletRequest request){
        return memberService.kakaoLogout(request);
    }


}
