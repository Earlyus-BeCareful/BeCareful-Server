package com.becareful.becarefulserver;

import org.springframework.boot.*;
import org.springframework.boot.autoconfigure.*;
import org.springframework.data.jpa.repository.config.*;
import org.springframework.scheduling.annotation.*;

@SpringBootApplication
@EnableJpaAuditing
@EnableScheduling
public class BecarefulServerApplication {
    public static void main(String[] args) {
        SpringApplication.run(BecarefulServerApplication.class, args);
    }
}
