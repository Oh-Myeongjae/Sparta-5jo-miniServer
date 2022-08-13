package com.sparta.sp5miniserver.service;

import com.amazonaws.services.s3.AmazonS3Client;
import com.sparta.sp5miniserver.dto.request.PostRequestDto;
import com.sparta.sp5miniserver.dto.response.ResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;

@Service
@RequiredArgsConstructor
public class PostService {

    private final AmazonS3Client amazonS3Client;

    @Value("${cloud.aws.s3.bucket}")  // 내 S3 버켓 이름!!
    private String bucketName;

    @Transactional   // 메소드 인수에 HttpServletRequest request 추가해줘야함.
    public ResponseDto<?> createPost(PostRequestDto postRequestDto, MultipartFile multipartFile) {

        String imageUrl = null;

        if(!multipartFile.isEmpty()){

        }

    }
}
