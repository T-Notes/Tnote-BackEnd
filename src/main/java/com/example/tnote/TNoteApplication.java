package com.example.tnote;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class TNoteApplication {

    public static void main(String[] args) {
        SpringApplication.run(TNoteApplication.class, args);
    }

}
