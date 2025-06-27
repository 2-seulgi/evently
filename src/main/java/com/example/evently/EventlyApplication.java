package com.example.evently;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableScheduling;

// Application 클래스 또는 설정 클래스
@EnableCaching
@EnableScheduling
@SpringBootApplication
public class EventlyApplication {

    public static void main(String[] args) {
        SpringApplication.run(EventlyApplication.class, args);
    }

}
