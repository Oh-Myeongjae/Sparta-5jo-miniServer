package com.sparta.sp5miniserver.controller;

import com.sparta.sp5miniserver.dto.response.ResponseDto;
import com.sparta.sp5miniserver.service.LikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequiredArgsConstructor
public class LikeController {

    private final LikeService likeService;

    @RequestMapping(value = "/api/post/like/{id}", method = RequestMethod.POST)
    public ResponseDto<?> likePost(
            @PathVariable Long id,
            HttpServletRequest request
    ) {
        return likeService.likePost(id, request);
    }

    @RequestMapping(value = "/api/comment/like/{id}", method = RequestMethod.POST)
    public ResponseDto<?> likeComment(
            @PathVariable Long id,
            HttpServletRequest request
    ) {
        return likeService.likeComment(id, request);
    }
}
