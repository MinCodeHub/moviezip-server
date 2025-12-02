package com.example.moviezip.util;

import com.example.moviezip.domain.CustomUserDetails;
import com.example.moviezip.service.CustomUserDetailsService;
import io.jsonwebtoken.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Component
public class jwtUtil {
    @Value("${jwt.secret}")
    private String SECRET_KEY;
    @Value("${token.access-token-expire-time}")
    private long ACCESS_TOKEN_EXPIRATION;
    @Value("${token.refresh-token-expire-time}")
    private long REFRESH_TOKEN_EXPIRATION;

     //JWT 토큰 생성
    public String createAccessToken(Long userId, String username, String role) {
        Claims claims = Jwts.claims().setSubject(username);
        claims.put("userId", userId);
        Date now = new Date();
        claims.put("role", role);
        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(new Date((new Date()).getTime() + ACCESS_TOKEN_EXPIRATION))
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
                .compact();
    }


    //JWT 토큰 생성
    public String createRefreshToken(Long userId, String username,  String role) {
        Claims claims = Jwts.claims().setSubject(username);
        claims.put("userId", userId);
        claims.put("role", role);
        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date())
                .setExpiration(new Date((new Date()).getTime() + REFRESH_TOKEN_EXPIRATION))
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
                .compact();
    }
    //accessToken 재발급
    public String refreshAccessToken(String refreshToken) {

        validateRefreshToken(refreshToken);

        Long userId = extractUserId(refreshToken);
        String username  = extractUsername(refreshToken);
        String role = extractRoles(refreshToken);

        return createAccessToken(userId,username,role);

    }

    //refreshToken 유효성 검증
    public void validateRefreshToken(String refreshToken) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(SECRET_KEY)
                    .build()
                    .parseClaimsJws(refreshToken);
            Claims claims = extractAllClaims(refreshToken);

            if (claims.getIssuedAt().after(new Date())) {
                throw new CustomException(ExceptionStatus.PREMATURE_TOKEN);
            }

            Date expireAt = claims.getExpiration();

            if (expireAt.before(new Date())) {
                throw new CustomException(ExceptionStatus.EXPIRED_TOKEN);
            }
        } catch (JwtException | IllegalArgumentException e) {
            throw new CustomException(ExceptionStatus.INVALID_TOKEN);
        }
    }


    public boolean validateToken(String jwtToken) {

        try {
            Jws<Claims> claims = Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(jwtToken);
            return !claims.getBody().getExpiration().before(new Date());
        } catch (Exception e) {
            return false;
        }
    }
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    //클레임 추출
    private Claims extractAllClaims(String token) {
        return Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(token).getBody();
    }

    //JWT에서 userId 추출
    public Long extractUserId(String token) {
        Claims claims = extractAllClaims(token); // JWT에서 모든 claims 가져오기
        return claims.get("userId", Long.class); // userId 추출 (Long 타입)
    }

    public String extractRoles(String token) {
        Claims claims = extractAllClaims(token);  // JWT에서 모든 claims 가져오기
        return (String) claims.get("role");  // roles는 이제 List<String> 형태로 저장됨
    }

    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }



}