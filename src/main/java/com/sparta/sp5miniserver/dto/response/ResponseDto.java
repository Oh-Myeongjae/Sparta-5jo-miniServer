package com.sparta.sp5miniserver.dto.response;


import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor // 모든 필드 값을 파라미터로 받는 생성자를 만듦
@Getter
public class ResponseDto<T> {
    private boolean success;
    private T data;
    private Error error;


    public static <T> ResponseDto<T> success(T data){
        return new ResponseDto<>( true,data,null);
    }

    public static <T> ResponseDto<T> fail(String code, String msg){
        return new ResponseDto<>( false, null, new Error(code,msg));
    }

    @Getter
    @AllArgsConstructor
    static class Error {
        private String code;
        private String message;
    }

}
