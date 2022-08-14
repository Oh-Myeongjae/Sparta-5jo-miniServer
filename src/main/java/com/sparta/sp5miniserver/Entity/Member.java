package com.sparta.sp5miniserver.entity;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Member extends Timestamped {
    //@Id
    //@GeneratedValue(strategy = GenerationType.IDENTITY)
    //private int id;
    @Id
    private String id;

    @Column(nullable = false)
    private String nickname;

    @Column(nullable = false)
    private String password;
}
