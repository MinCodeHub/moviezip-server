package com.example.moviezip.configs;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration //이 클래스가 Spring 설정 클래스임을 나타냄
@EnableWebSecurity //Spring Security를 활성화
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("http://localhost:3001") // 이 주소에서 오는 요청을 허용
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // 허용되는 HTTP 메서드
                .allowedHeaders("Authorization", "Content-Type") // 허용되는 헤더
                .exposedHeaders("Custom-Header") // 응답 헤더 중 클라이언트에 노출할 헤더
                .allowCredentials(true) // 자격 증명(쿠키 등) 허용
                .maxAge(3600); // CORS preflight 요청의 캐시 시간 (1시간)
    }
}