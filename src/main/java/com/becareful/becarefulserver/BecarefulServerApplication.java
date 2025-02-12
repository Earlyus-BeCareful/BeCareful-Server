package com.becareful.becarefulserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class BecarefulServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(BecarefulServerApplication.class, args);
    }

}
