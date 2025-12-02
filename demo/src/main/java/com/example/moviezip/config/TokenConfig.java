//package com.example.moviezip.config;
//
//import lombok.Data;
//import org.springframework.boot.context.properties.ConfigurationProperties;
//import org.springframework.context.annotation.Configuration;
//
///**
// * JWT 토큰 관련 설정값을 application.yml에서 읽어오는 클래스입니다.
// * 모든 속성은 'token' 접두사를 사용합니다.
// */
//@Configuration
//@ConfigurationProperties(prefix = "token")
//@Data
//public class TokenConfig {
//    // 토큰 서명에 사용되는 비밀 키
//    private String secret;
//
//    // 액세스 토큰 만료 시간 (밀리초)
//    private Long accessTokenExpireTime;
//
//    // 리프레시 토큰 만료 시간 (밀리초)
//    private Long refreshTokenExpireTime;
//}