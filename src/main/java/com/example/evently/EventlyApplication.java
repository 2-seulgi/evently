package com.example.evently;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

// Application 클래스 또는 설정 클래스
@EnableCaching
@SpringBootApplication
public class EventlyApplication {

    public static void main(String[] args) {
        SpringApplication.run(EventlyApplication.class, args);
    }

}
