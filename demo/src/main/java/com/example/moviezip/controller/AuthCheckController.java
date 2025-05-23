package com.example.moviezip.controller;

import com.example.moviezip.service.CustomUserDetailsService;
import com.example.moviezip.util.jwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AuthCheckController {

    private final jwtUtil jwtutil;
    private final CustomUserDetailsService customUserDetailsService;

    @GetMapping("/auth-check")
    public boolean isAuthenticated(@RequestHeader("Authorization") String token) {
        String jwt = token.substring(7); // "Bearer " 제거
        // 토큰 검증 및 사용자 정보 추출 (예: JWT에서 userId 추출)
        String userNameFromToken = jwtutil.extractUsername(jwt); // jwtUtil은 JWT 유틸리티 클래스

        // 토큰 유효성 검사
        if (!jwtutil.validateToken(jwt)) {
            throw new RuntimeException("Invalid or expired token");  // 적절한 예외 처리
        }
        return true; // 인증 실패
    }
}
