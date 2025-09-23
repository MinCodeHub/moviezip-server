package com.example.moviezip.controller;


import com.example.moviezip.domain.CustomUserDetails;
import com.example.moviezip.domain.RefreshToken;

import com.example.moviezip.domain.jwt.AuthenticationRequest;
import com.example.moviezip.domain.jwt.AuthenticationResponse;
import com.example.moviezip.service.CustomUserDetailsService;
import com.example.moviezip.service.RefreshTokenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import com.example.moviezip.util.jwtUtil;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
public class LoginController {

    private final AuthenticationManager authenticationManager;
    private final jwtUtil jwtTokenUtil;
    private final RefreshTokenService refreshTokenService;
    @Value("${token.refresh-token-expire-time}")
    private long refreshTokenExpirationTime;
    private final CustomUserDetailsService userDetailsService;

    //사용자의 id,pw를 검증
    //jwtUtil을 호출해 Token을 생성하고 JwtResponse에 Token을 담아 return ResponseEntity
    @PostMapping("/authenticate")
    public ResponseEntity<?> createAuthenticationToken(@RequestBody AuthenticationRequest authenticationRequest,
                                                       HttpServletResponse response) throws Exception {

        try {
            // 1. 사용자 인증
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            authenticationRequest.getUsername(),
                            authenticationRequest.getPassword())
            );
            // 2. 인증된 사용자 정보
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            Long userId = userDetails.getUserId(); // 또는 getUserId()
            // 3. 사용자 ID 및 Role 추출
            String username = userDetails.getUsername();

            String role = userDetails.getAuthorities().stream()
                    .findFirst()
                    .map(GrantedAuthority::getAuthority)
                    .orElse("USER"); // 기본값

            //final String accessToken = jwtTokenUtil.createAccessToken(userDetails);
            // 4. JWT 생성
            final String accessToken = jwtTokenUtil.createAccessToken(userId, username,role);

            RefreshToken refreshToken = refreshTokenService.createOrUpdateRefreshToken(userDetails, response);

            // Refresh Token을 HttpOnly 쿠키에 저장
            Cookie cookie = new Cookie("refreshToken", refreshToken.getToken());
            cookie.setHttpOnly(true);    // JavaScript에서 접근 불가
            cookie.setSecure(true);      // HTTPS 환경에서만 전송되도록 설정 (필요한 경우)
            cookie.setPath("/");         // 모든 경로에서 사용 가능하도록 설정
            cookie.setMaxAge((int) refreshTokenExpirationTime);  // 유효 기간 설정
            response.addCookie(cookie);
            log.info("쿠키 설정: refreshToken={}, MaxAge={}", refreshToken.getToken(), refreshTokenExpirationTime);
            return ResponseEntity.ok(new AuthenticationResponse(accessToken));

        } catch (BadCredentialsException e) {
            throw new Exception("올바르지 않은 아이디와 비밀번호", e);
        }
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshAccessToken(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String refreshToken = null;

        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("refreshToken".equals(cookie.getName())) {
                    refreshToken = cookie.getValue();
                    break;
                }
            }
        }

        // refreshToken이 없으면 예외 처리
        if (refreshToken == null) {
            throw new Exception("Refresh token이 없습니다.");
        }
        log.info("Received refresh token: " + refreshToken);

        // 후속 과정으로 유효성 검사 등 계속 진행...
        // refreshToken이 유효하면 새로운 access token 발급

        String newAccessToken = null;
        if (refreshTokenService.validateRefreshToken(refreshToken)) {
            newAccessToken = jwtTokenUtil.refreshAccessToken(refreshToken);
        }
        String username = jwtTokenUtil.extractUsername(refreshToken);

        UserDetails userDetails = userDetailsService.loadUserByUsername(username);

        refreshTokenService.createOrUpdateRefreshToken(userDetails, response);

        // access token을 JSON 응답으로 반환
        return ResponseEntity.ok(new AuthenticationResponse(newAccessToken));

    }
}
