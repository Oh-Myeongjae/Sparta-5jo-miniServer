package com.sparta.sp5miniserver.service;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.sparta.sp5miniserver.dto.request.PostRequestDto;
import com.sparta.sp5miniserver.dto.response.PostResponseDto;
import com.sparta.sp5miniserver.dto.response.ResponseDto;
import com.sparta.sp5miniserver.entity.Post;
import com.sparta.sp5miniserver.repository.PostRepository;
import com.sparta.sp5miniserver.utils.CommonUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;


@Service
@RequiredArgsConstructor
public class PostService {

    private final AmazonS3Client amazonS3Client;
    private final PostRepository postRepository;

    @Value("${cloud.aws.s3.bucket}")  // 내 S3 버켓 이름!!
    private String bucketName;

    @Transactional   // 메소드 인수에 HttpServletRequest request 추가해줘야함.
    public ResponseDto<?> createPost(PostRequestDto postRequestDto, MultipartFile multipartFile) throws IOException {

        String imageUrl = null;  // 입력 이미지가 없다면!!
        // 참고 사이트 : https://www.sunny-son.space/spring/Springboot%EB%A1%9C%20S3%20%ED%8C%8C%EC%9D%BC%20%EC%97%85%EB%A1%9C%EB%93%9C/
        if (!multipartFile.isEmpty()) { // 이미지가 있다면!!
        String fileName = CommonUtils.buildFileName(multipartFile.getOriginalFilename()); // 파일이름
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentType(multipartFile.getContentType());  // 이게 머지? : 파일타입
        InputStream inputStream = multipartFile.getInputStream();   // 이게 머지??? :
        amazonS3Client.putObject(new PutObjectRequest(bucketName, fileName, inputStream, objectMetadata)
                .withCannedAcl(CannedAccessControlList.PublicRead));  // S3 저장 및 권한설정

        imageUrl = amazonS3Client.getUrl(bucketName, fileName).toString(); // URL 대입!
        }


        Post post = Post.builder()
                .title(postRequestDto.getTitle())
                .content(postRequestDto.getContent())
                .imageUrl(imageUrl)
                .build();
        postRepository.save(post);  // 저장까지는 되거든?? 왜 405가 뜰까 >> Respondto Getter추가 안해서 !!

        return ResponseDto.success(
                PostResponseDto.builder()
                        .id(post.getId())
                        .title(post.getTitle())
                        .content(post.getContent())
                        .imageUrl(post.getImageUrl())
                        .createAt(post.getCreatedAt())
                        .modifiedAt(post.getModifiedAt())
                        .build()
        );



    }

    @Transactional(readOnly = true)
    public ResponseDto<?> getAllPost() {
        List<Post> postList = postRepository.findAllByOrderByModifiedAtDesc();
        List<PostResponseDto> dtoList = new ArrayList<>();

        for(Post post : postList){
            dtoList.add( PostResponseDto.builder()
                    .id(post.getId())
                    .title(post.getTitle())
                    .content(post.getContent())
                    .imageUrl(post.getImageUrl())
                    .createAt(post.getCreatedAt())
                    .modifiedAt(post.getModifiedAt())
                    .build());
        }

        return ResponseDto.success(dtoList);

    }
}
