package com.example.jobapplication;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

/**
 * Main Spring Boot Application class
 */
@SpringBootApplication
@EnableCaching
public class JobApplicationApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(JobApplicationApiApplication.class, args);
    }
}

