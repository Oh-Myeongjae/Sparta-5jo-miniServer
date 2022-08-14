package com.sparta.sp5miniserver.controller;

import com.sparta.sp5miniserver.dto.request.LoginRequestDto;
import com.sparta.sp5miniserver.dto.response.ResponseDto;
import com.sparta.sp5miniserver.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import javax.servlet.http.HttpServletResponse;

@RequiredArgsConstructor
@RestController
public class LoginController {

    private final MemberService memberService;

    @PostMapping(value = "/api/login")
    public ResponseDto<?> login(@RequestBody LoginRequestDto requestDto, HttpServletResponse response) {
        System.out.println("requestDto.getId() = " + requestDto.getId());
        System.out.println("requestDto = " + requestDto.getPassword());
        return memberService.login(requestDto, response);
    }

}
