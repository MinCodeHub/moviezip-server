package com.example.moviezip.util;

import com.example.moviezip.service.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.lang.NonNull;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
@Order(Ordered.HIGHEST_PRECEDENCE + 99) //구성된 인터셉터들 간의 작업 우선순위를 지정
public class FilterChannelInterceptor implements ChannelInterceptor {

    private final jwtUtil jwtutil;

    private final CustomUserDetailsService customUserDetailsService;
    // websocket을 통해 들어온 요청이 처리 되기전 실행된다.
    @Override
    public Message<?> preSend(@NonNull Message<?> message, @NonNull MessageChannel channel) {

        //STOMP의 헤더에 직접 접근
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(message);

        //StompHeaderAccessor headerAccessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        log.info(">>>>>> headerAccessor : {}", headerAccessor);
        log.info(">>>>> headAccessorHeaders : {}", headerAccessor.getCommand());

        // CONNECT 명령에 대해서만 처리
        if (StompCommand.CONNECT.equals(headerAccessor.getCommand())) {
            String rawToken = headerAccessor.getFirstNativeHeader("Authorization");

            if (rawToken  == null || rawToken .isBlank()) {
                throw new CustomException(ExceptionStatus.INVALID_TOKEN);
            }
            log.info("받은 토큰 = {}", rawToken );
            String token = removeBrackets(rawToken);
            try {

                if (jwtutil.validateToken(token)) {  //토큰 검증
                    Long userId = jwtutil.extractUserId(token);
                    String username = jwtutil.extractUsername(token);
                    UserDetails userDetails = customUserDetailsService.loadUserByUsername(username);
                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    log.info(">>>> UserDetails 조회 결과: {}", userDetails);
                    log.info(">>>> 설정된 Principal: {}", authentication);
                    headerAccessor.setUser(authentication); // WebSocket용 Principal 설정

                }
            } catch (Exception e) {
                log.info("토큰 검증 실패 = {}", e.getMessage());
                throw new CustomException(ExceptionStatus.INVALID_TOKEN);
            }
        }


        return message;

    }

    // Authorization 헤더에서 대괄호([])를 제거하는 메서드
    public String removeBrackets(String token) {
        // Trim and log the token before processing
        token = token.trim();
        log.info("Token before processing: {}", token);  // 디버깅 로그 추가


        if (token.startsWith("[") && token.endsWith("]")) {
            token = token.substring(1, token.length() - 1);
            log.info("Token after removing brackets: {}", token);  // 대괄호 제거 후 토큰 로그
        }

        if (token.startsWith("Bearer ")) {
            token = token.substring(7); // "Bearer " (7 characters) 제거
            log.info("Token after removing Bearer: {}", token);  // Bearer 제거 후 토큰 로그
        }

        return token;
    }


}