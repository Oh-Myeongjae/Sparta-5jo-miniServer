package com.sparta.sp5miniserver.Entity;


import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Setter
@Getter
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
