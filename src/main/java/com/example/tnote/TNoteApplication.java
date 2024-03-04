package com.example.tnote;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableJpaAuditing
@EnableScheduling
public class TNoteApplication {

    public static void main(String[] args) {

        // SSL test
        SpringApplication.run(TNoteApplication.class, args);
    }
}
