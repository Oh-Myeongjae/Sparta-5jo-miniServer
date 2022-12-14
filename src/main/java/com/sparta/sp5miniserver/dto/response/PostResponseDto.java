package com.sparta.sp5miniserver.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PostResponseDto {
    private Long id;
    private String title;
    private String content;
    private String imageUrl;
    private LocalDateTime createAt;
    private LocalDateTime modifiedAt;
    private List<CommentListDto> commentList;
    private int likesCount;  //받은 좋아요의 개수

}
