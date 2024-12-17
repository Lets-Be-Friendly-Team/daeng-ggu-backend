package com.ureca.config.web;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        // 프론트엔드 도메인에 대해서 CORS를 허용
        registry.addMapping("/**") // 모든 경로에 대해
                .allowedOrigins(
                        "https://ds1c375x1a64n.cloudfront.net",
                        "http://localhost:5173",
                        "http://localhost:5174",
                        "http://localhost:5175",
                        "https://localhost:5173",
                        "https://localhost:5174",
                        "https://localhost:5175",
                        "https://www.daeng-ggu-backend.com",
                        "https://www.daeng-ggu.com") // 허용할 도메인
                .allowedMethods("GET", "POST", "PUT", "DELETE", "PATCH") // 허용할 HTTP 메소드
                .allowedHeaders("*") // 허용할 헤더
                .allowCredentials(true); // 쿠키 등을 허용할지 여부
    }
}
