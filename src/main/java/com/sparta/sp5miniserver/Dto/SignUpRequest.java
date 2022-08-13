package com.sparta.sp5miniserver.dto;

import lombok.Getter;

import java.util.regex.Pattern;

@Getter
public class SignUpRequest {
    //validate 함수를 만든다
    private String memberId;

    private String nickname;

    private String password;

    public void validate() {

        String pattern = "^[가-힣\\da-zA-Z]*${2,10}";
        String patternPassword = "(?=.*\\d).{2,16}";
        //(?=.*[a-zA-Z])(?=.*\\W)(?=\\S+$)

        if(!Pattern.matches(pattern, memberId)){
            throw new IllegalArgumentException("아이디는 2~10자리의 영문 대소문자여야 합니다");
        }
        if(!Pattern.matches(pattern, nickname)){
            throw new IllegalArgumentException("닉네임은 특수문자를 제외한 2~10자리여야 합니다");
        }
        if(!Pattern.matches(patternPassword, password)){
            throw new IllegalArgumentException("비밀번호는 2~16자의 숫자");
        }
    }
}
