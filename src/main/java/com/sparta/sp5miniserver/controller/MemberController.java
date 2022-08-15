package com.sparta.sp5miniserver.controller;

import com.sparta.sp5miniserver.Dto.SignUpRequest;
import com.sparta.sp5miniserver.Service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MemberController {
    private final MemberService memberService;

    @Autowired
    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }


    @PostMapping("/member/signup")
    public void signUp(@RequestBody SignUpRequest request){
        memberService.signUp(request);
    }
}
