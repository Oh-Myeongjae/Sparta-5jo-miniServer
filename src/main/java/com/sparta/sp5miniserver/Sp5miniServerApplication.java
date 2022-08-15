package com.sparta.sp5miniserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class Sp5miniServerApplication {
    public static void main(String[] args) {
        SpringApplication.run(Sp5miniServerApplication.class, args);
    }

}
