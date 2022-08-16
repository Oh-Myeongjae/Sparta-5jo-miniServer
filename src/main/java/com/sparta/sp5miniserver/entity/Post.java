package com.sparta.sp5miniserver.entity;


import com.sparta.sp5miniserver.dto.request.PostRequestDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;


@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class Post extends Timestamped {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String content;
    @Column
    private String imageUrl;

    @Transient    // 만들어보자!!
    private Long heartCount;

//    // 임시로 멤버 연관관계 삭제
//    @JoinColumn(name = "member_id", nullable = false)
//    @ManyToOne(fetch = FetchType.LAZY)
//    private Member member;

    @OneToMany( mappedBy = "post", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> commentList;

//    @OneToMany( mappedBy = "post", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
//    private List<HeartPost> heartPostList;

    //좋아요
    @OneToMany(mappedBy = "post", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    private List<PostLike> postLikeList;

    
    public void update(PostRequestDto postRequestDto, String imageUrl){
        this.title = postRequestDto.getTitle();
        this.content = postRequestDto.getContent();
        this.imageUrl = imageUrl;
    }

}
