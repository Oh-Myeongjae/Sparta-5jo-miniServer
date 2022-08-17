package com.sparta.sp5miniserver.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PageDto {
       private Long TotalElement;
       private int TotalPages;
       private int NowPage;
       private int NowContent;
       private int Size;
       private List<PostResponseDto>  content;

}
