package com.sparta.sp5miniserver.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CommentListDto {
    private Long id;
    private String author;
    private String content;
    private LocalDateTime createAt;
    private LocalDateTime modifiedAt;
}
