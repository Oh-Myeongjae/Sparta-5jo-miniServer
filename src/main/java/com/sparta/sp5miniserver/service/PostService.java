package com.sparta.sp5miniserver.service;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.sparta.sp5miniserver.dto.request.PostRequestDto;
import com.sparta.sp5miniserver.dto.response.CommentListDto;
import com.sparta.sp5miniserver.dto.response.PageDto;
import com.sparta.sp5miniserver.dto.response.PostResponseDto;
import com.sparta.sp5miniserver.dto.response.ResponseDto;
import com.sparta.sp5miniserver.entity.*;
import com.sparta.sp5miniserver.repository.CommentRepository;
import com.sparta.sp5miniserver.repository.PostLikeRepository;
import com.sparta.sp5miniserver.repository.PostRepository;
import com.sparta.sp5miniserver.utils.CommonUtils;
import com.sparta.sp5miniserver.utils.jwt.TokenProvider;
import lombok.RequiredArgsConstructor;
import org.apache.catalina.util.URLEncoder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
@Service
@RequiredArgsConstructor
public class PostService {

    private final AmazonS3Client amazonS3Client;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final PostLikeRepository postLikeRepository;
    private final TokenProvider tokenProvider;


    @Value("${cloud.aws.s3.bucket}")  // ??? S3 ?????? ??????!!
    private String bucketName;

    @Transactional
    public ResponseDto<?> createPost(PostRequestDto postRequestDto, UserDetailsImpl userDetails) throws IOException {

        Member member = userDetails.getMember();
        MultipartFile multipartFile = postRequestDto.getImage();

        String imageUrl;  // ?????? ???????????? ?????????!!
        // ?????? ????????? : https://www.sunny-son.space/spring/Springboot%EB%A1%9C%20S3%20%ED%8C%8C%EC%9D%BC%20%EC%97%85%EB%A1%9C%EB%93%9C/
        if (!multipartFile.isEmpty()) { // ???????????? ?????????!!
        String fileName = CommonUtils.buildFileName(multipartFile.getOriginalFilename()); // ????????????
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentType(multipartFile.getContentType());  // ?????? ??????? : ????????????
        InputStream inputStream = multipartFile.getInputStream();   // ?????? ????????? : ?????? ???????????? ????????????~
        amazonS3Client.putObject(new PutObjectRequest(bucketName, fileName, inputStream, objectMetadata)
                .withCannedAcl(CannedAccessControlList.PublicRead));  // S3 ?????? ??? ????????????

        imageUrl = amazonS3Client.getUrl(bucketName, fileName).toString(); // URL ??????!, URL ????????? ????????????

        }else{
            String fileName = "animalDefault.png";
            imageUrl = amazonS3Client.getUrl(bucketName, fileName).toString(); // URL ??????!
        }




        Post post = Post.builder()
                .title(postRequestDto.getTitle())
                .member(member)
                .content(postRequestDto.getContent())
                .imageUrl(imageUrl)
                .build();
        postRepository.save(post);  // ??????????????? ??????????? ??? 405??? ?????? >> Respondto Getter?????? ????????? !!

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
    public ResponseDto<?> getAllPost(int page, int size, String sortBy) {

        Sort.Direction direction = Sort.Direction.DESC; // true: ???????????? (asc) , ???????????? DESC(?????? ?????? ????????????)
        Sort sort = Sort.by(direction, sortBy);
        Pageable pageable = PageRequest.of(page, size,sort);
        Page<Post> postList = postRepository.findAll(pageable);

//        List<Post> postList = postRepository.findAllByOrderByModifiedAtDesc();
        List<PostResponseDto> dtoList = new ArrayList<>();

        for(Post post : postList){
            dtoList.add( PostResponseDto.builder()
                    .id(post.getId())
                    .title(post.getTitle())
                    .content(post.getContent())
                    .imageUrl(post.getImageUrl())
                    .createAt(post.getCreatedAt())
                    .modifiedAt(post.getModifiedAt())
                    .likesCount(countLikesPost(post))
                    .build());
        }
        PageDto pageDto = PageDto.builder()
                .TotalElement(postList.getTotalElements())
                .TotalPages(postList.getTotalPages())
                .NowPage(postList.getNumber()+1)
                .NowContent(postList.getNumberOfElements())
                .content(dtoList)
                .build();
        return ResponseDto.success(pageDto);

    }

    @Transactional(readOnly = true)  // ????????? ????????? HttpServletRequest request ??????????????????. ??? ????????? ????????? ???????????? ????????? ???????
    public ResponseDto<?> getOnePost(Long postId) {
//        Post post = postRepository.findById(postId).orElseThrow(()
//                -> new NullPointerException("?????? ???????????? ???????????? ????????????.")); ///
//           ???????????? ?????? :  null????????? ?????? ??????????????? post orElseThrow??? ??????????????? RestControllerAdvice?????? ????????? ??? ?????? ????????????.
        Optional<Post> OptionalPost= postRepository.findById(postId);

        if(OptionalPost.isEmpty()){
            return ResponseDto.fail("????????????~~","???????????? ?????? ?????????");
        }

        Post post = OptionalPost.get();
        List<Comment> commentList = commentRepository.findAllByPost(post);  // ????????? ?????? ?????? ?????? ?????????!
        List<CommentListDto> commentResponseDtoList = new ArrayList<>();
        for(Comment comment  : commentList){
            commentResponseDtoList.add(
                    CommentListDto.builder()
                    .id(comment.getId())
                    .content(comment.getContent())
                    .author(comment.getMember().getNickname())
                    .createAt(comment.getCreatedAt())
                    .modifiedAt(comment.getModifiedAt())
                    .build()
            );
        }


        PostResponseDto postResponseDto = PostResponseDto.builder()
                .id(post.getId())
                .title(post.getTitle())
                .content(post.getContent())
                .imageUrl(post.getImageUrl())
                .createAt(post.getCreatedAt())
                .modifiedAt(post.getModifiedAt())
                .commentList(commentResponseDtoList)
                .likesCount(countLikesPost(post))//????????? ??????
                .build();
        return ResponseDto.success(postResponseDto);
    }

    @Transactional
    public ResponseDto<?> updatePost(Long postId, PostRequestDto postRequestDto, UserDetailsImpl userDetails) throws IOException {

        Member member = userDetails.getMember();

        String imageUrl = null;  // ????????? ?????????..
        MultipartFile multipartFile = postRequestDto.getImage();

        Optional<Post> optionalPost =  postRepository.findById(postId);

        if(optionalPost.isEmpty()){
            return ResponseDto.fail("????????????~~","???????????? ?????? ?????????");
        }

        Post post = optionalPost.get();

        if(post.getMember().getId()!= member.getId()){
            return ResponseDto.fail("????????????~~","????????????????????? ?????????????????????");
        }

        if(post.getImageUrl().equals("https://spartabucketson.s3.ap-northeast-2.amazonaws.com/animalDefault.png")){
            // ?????? ???????????? ???????????? ?????? ??????!!

            if (!multipartFile.isEmpty()) {  // ?????????????????? ???????????? ????????????
                String fileName = CommonUtils.buildFileName(multipartFile.getOriginalFilename());
                ObjectMetadata objectMetadata = new ObjectMetadata();
                objectMetadata.setContentType(multipartFile.getContentType());
                InputStream inputStream = multipartFile.getInputStream();
                amazonS3Client.putObject(new PutObjectRequest(bucketName, fileName, inputStream, objectMetadata)
                        .withCannedAcl(CannedAccessControlList.PublicRead));
                imageUrl = amazonS3Client.getUrl(bucketName, fileName).toString();
            } else {          // ?????? ???????????? ???????????? ????????? ??????
                String fileName = "animalDefault.png";
                imageUrl = amazonS3Client.getUrl(bucketName, fileName).toString(); // URL ??????!
            }
        }
        else { //?????? ???????????? ???????????? ?????? ??????!!
            String urlFileName = post.getImageUrl().substring(56);
            urlFileName = URLDecoder.decode(urlFileName,"UTF-8"); // ?????? ?????? ?????? ?????????!!
            fileDelete(urlFileName); // S3??? ???????????? ????????? ??????!


            if (!multipartFile.isEmpty()) {  // ?????????????????? ???????????? ????????????
                String fileName = CommonUtils.buildFileName(multipartFile.getOriginalFilename());
                ObjectMetadata objectMetadata = new ObjectMetadata();
                objectMetadata.setContentType(multipartFile.getContentType());
                InputStream inputStream = multipartFile.getInputStream();
                amazonS3Client.putObject(new PutObjectRequest(bucketName, fileName, inputStream, objectMetadata)
                        .withCannedAcl(CannedAccessControlList.PublicRead));
                imageUrl = amazonS3Client.getUrl(bucketName, fileName).toString();
            } else {    // ?????? ???????????? ???????????? ????????? ??????
                String fileName = "animalDefault.png";
                imageUrl = amazonS3Client.getUrl(bucketName, fileName).toString(); // URL ??????!
            }

        }

        post.update(postRequestDto,imageUrl,member); // ????????????!

        List<CommentListDto> commentResponseDtoList = new ArrayList<>();
        for(Comment comment  : post.getCommentList()){
            commentResponseDtoList.add(
                    CommentListDto.builder()
                            .id(comment.getId())
                            .content(comment.getContent())
                            .author(comment.getMember().getNickname())
                            .createAt(comment.getCreatedAt())
                            .modifiedAt(comment.getModifiedAt())
                            .build()
            );
        }

        return ResponseDto.success(
                PostResponseDto.builder()
                        .id(post.getId())
                        .title(post.getTitle())
                        .content(post.getContent())
                        .imageUrl(post.getImageUrl())
                        .createAt(post.getCreatedAt())
                        .modifiedAt(post.getModifiedAt())
                        .commentList(commentResponseDtoList)
                        .build()
        );
    }

    @Transactional
    public ResponseDto<?> deletePost(Long postId, UserDetailsImpl userDetails) throws UnsupportedEncodingException {
        Member member = userDetails.getMember();

        Optional<Post> optionalPost =  postRepository.findById(postId);

        if(optionalPost.isEmpty()){
            return ResponseDto.fail("????????????~~","???????????? ?????? ?????????");
        }
        Post post = optionalPost.get();

        if(post.getMember().getId()!= member.getId()){
            return ResponseDto.fail("????????????~~","????????????????????? ?????????????????????");
        }

        if(!post.getImageUrl().equals("https://spartabucketson.s3.ap-northeast-2.amazonaws.com/animalDefault.png")){
            String urlFileName = post.getImageUrl().substring(56); // ?????? ???????????? ?????? ?????? ??????
            urlFileName = URLDecoder.decode(urlFileName,"UTF-8"); // ?????? ?????? ?????? ?????????!!
            fileDelete(urlFileName);
        }

            postRepository.delete(post);


        return ResponseDto.success("????????? ?????? ??????!");
    }




    @Transactional(readOnly = true) //?????????!!
    public Post isPresentPost(Long id) {
        Optional<Post> optionalPost = postRepository.findById(id);
        return optionalPost.orElse(null);
    }

  // S3 ????????? ??????
    public void fileDelete(String fileName) {

        try {
            amazonS3Client.deleteObject(this.bucketName, (fileName).replace(File.separatorChar, '/'));
        } catch (AmazonServiceException e) {
            System.err.println(e.getErrorMessage());
        }
    }

    //???????????? ????????? ????????? ??????
    @Transactional(readOnly = true)
    public int countLikesPost(Post post) {
        List<PostLike> postLikeList = postLikeRepository.findAllByPost(post);
        return postLikeList.size();
    }


}
