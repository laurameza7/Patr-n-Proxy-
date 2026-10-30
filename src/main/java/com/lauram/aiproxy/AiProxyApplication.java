package com.lauram.aiproxy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class AiProxyApplication {

    public static void main(String[] args) {
        SpringApplication.run(AiProxyApplication.class, args);
    }
}
