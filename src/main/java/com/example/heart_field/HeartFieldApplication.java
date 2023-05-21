package com.example.heart_field;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class HeartFieldApplication {
    public static void main(String[] args) {
        SpringApplication.run(HeartFieldApplication.class, args);
    }
}
