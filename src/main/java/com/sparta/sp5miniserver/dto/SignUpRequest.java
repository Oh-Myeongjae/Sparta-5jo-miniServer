package com.sparta.sp5miniserver.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.regex.Pattern;

@Getter
@NoArgsConstructor
public class SignUpRequest {

    @NotBlank
    private String memberId;

    @NotBlank
    private String nickname;

    @NotBlank
    private String password;

    //validate 함수를 만든다
    public String validate() {

        String pattern = "^[가-힣\\da-zA-Z]*${5,12}"; /// 유효성검사.
        String patternPassword = "(?=.*\\d).{8,20}";
        //(?=.*[a-zA-Z])(?=.*\\W)(?=\\S+$)

        if(!Pattern.matches(pattern, memberId)){
            return "아이디는 6자리 이상의 영문 대소문자여야 합니다";
        }
        if(!Pattern.matches(pattern, nickname)){
            return "닉네임은 특수문자를 제외한 2~10자리여야 합니다";
        }
        if(!Pattern.matches(patternPassword, password)){
            return "비밀번호는 2~16자의 숫자";
        }
        return "정상";
    }
}
