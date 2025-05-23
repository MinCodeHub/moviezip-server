package com.example.moviezip.service;

import com.example.moviezip.domain.CustomUserDetails;
import com.example.moviezip.domain.RefreshToken;
import com.example.moviezip.repository.RefreshTokenRepository;
import com.example.moviezip.util.jwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.redis.core.RedisTemplate;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;


@Slf4j
@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final jwtUtil jwtTokenUtil;
    @Value("${token.refresh-token-expire-time}")
    private long REFRESH_TOKEN_EXPIRATION;

    //RefreshToken 저장(이미 있으면 갱신)
    @Transactional
    public RefreshToken createOrUpdateRefreshToken(UserDetails userDetails, HttpServletResponse response) {
        CustomUserDetails customUserDetails = (CustomUserDetails) userDetails;
        Long userId = customUserDetails.getUserId();  // 명확한 타입 사용
        String username = customUserDetails.getUsername();
        String role = userDetails.getAuthorities().stream()
                .findFirst()
                .map(GrantedAuthority::getAuthority)
                .orElse("USER"); // 기본값

        String refreshToken = jwtTokenUtil.createRefreshToken(userId,username,role);
        LocalDateTime expiryDate = LocalDateTime.now().plus(Duration.ofMillis(REFRESH_TOKEN_EXPIRATION));

        RefreshToken token = refreshTokenRepository.findByUserId(userId)
                .map(existing -> {
                    existing.updateToken(refreshToken, expiryDate);
                    return existing;
                })
                .orElseGet(() -> RefreshToken.builder()
                        .userId(userId)
                        .token(refreshToken)
                        .expiryDate(expiryDate)
                        .build());

        refreshTokenRepository.save(token);

        Cookie cookie = new Cookie("refreshToken", token.getToken());
        cookie.setHttpOnly(true);
        cookie.setSecure(isProduction()); // 환경 기반 설정 추천
        cookie.setPath("/");
        cookie.setMaxAge((int) REFRESH_TOKEN_EXPIRATION);
        response.addCookie(cookie);

        return token;
    }

    private boolean isProduction() {
        return true; // 환경에 따라 동적으로 결정 (예: prod profile이면 true)
    }

    // Refresh Token 검증
    public boolean validateRefreshToken(String token) {
        Optional<RefreshToken> refreshTokenOpt = refreshTokenRepository.findByToken(token.trim());
        if (refreshTokenOpt.isEmpty()) {
            log.info("Token not found in DB. Incoming token: " + token);
            return false;
        }
        RefreshToken refreshToken = refreshTokenOpt.get();
        log.info("Token from DB: " + refreshToken.getToken());
        return !refreshToken.isExpired();
    }


    //Refresh Token 조회
    public Optional<RefreshToken> getRefreshToken(String token) {
        return refreshTokenRepository.findByToken(token);
    }

    //특정 사용자의 Refresh Token 삭제
    @Transactional
    public void deleteRefreshToken(Long userId) {
        refreshTokenRepository.deleteByUserId(userId);
    }
}

