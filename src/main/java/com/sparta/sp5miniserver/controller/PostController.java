package com.sparta.sp5miniserver.controller;

import com.sparta.sp5miniserver.dto.request.PostRequestDto;
import com.sparta.sp5miniserver.dto.response.ResponseDto;
import com.sparta.sp5miniserver.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RequiredArgsConstructor // 생성자 주입
@RestController
@RequestMapping("/api")
public class PostController {

    private final PostService postService;

    @PostMapping("/post")  // <form> 요소가 파일이나 이미지를 서버로 전송할 때 주로 사용!!  HttpServletRequest request 추가해줘야함.
    public ResponseDto<?> createPost(@RequestPart(value ="post") PostRequestDto postRequestDto,
                                     @RequestPart(value = "image") MultipartFile multipartFile) throws IOException {
        return postService.createPost(postRequestDto,multipartFile);}

    @GetMapping("/posts") // 게시판 전체 조회
    public ResponseDto<?> getAllPosts(){
        return postService.getAllPost();
    }

    @GetMapping("/post/{postId}") // 게시글 한개 조회
    public ResponseDto<?> getOnePost(@PathVariable Long postId){
        return postService.getOnePost(postId);
    }

    @PutMapping("/post/{postId}") // 게시글 수정 (추후에 시간이 된다면 패치로 수정해보는 것도 나쁘진 않을듯!!)
    public ResponseDto<?> updatePost(@PathVariable Long postId,
                                     @RequestPart(value ="post") PostRequestDto postRequestDto,
                                     @RequestPart(value = "image") MultipartFile multipartFile) throws IOException {
        return postService.updatePost(postId,postRequestDto,multipartFile);
    }

    @DeleteMapping("/post/{postId}")
    public ResponseDto<?> deletePost(@PathVariable Long postId){
        return postService.deletePost(postId);
    }


}
