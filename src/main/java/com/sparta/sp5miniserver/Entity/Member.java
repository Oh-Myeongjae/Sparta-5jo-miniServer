package com.sparta.sp5miniserver.Entity;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Setter
@Getter
@NoArgsConstructor
@Entity
public class Member extends Timestamped {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false)
    private String memberId;

    @Column(nullable = false)
    private String nickname;

    @Column(nullable = false)
    private String password;
}