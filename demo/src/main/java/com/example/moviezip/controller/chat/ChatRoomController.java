package com.example.moviezip.controller.chat;

import com.example.moviezip.domain.chat.ChatRoom;
import com.example.moviezip.service.CustomUserDetailsService;
import com.example.moviezip.service.UserServiceImpl;
import com.example.moviezip.service.chat.ChatRoomService;
import com.example.moviezip.util.jwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequiredArgsConstructor
@RequestMapping("/chat")
public class ChatRoomController {
    private final ChatRoomService chatRoomService;
    private final jwtUtil jwtUtil;
    private final UserServiceImpl userService;
    private final CustomUserDetailsService customUserDetailsService;

    //채팅방 조회
    @GetMapping
    public ResponseEntity<List<ChatRoom>> getChatRooms(@RequestHeader("Authorization") String token) {

        // JWT 토큰에서 Bearer 제거
        String jwt = token.substring(7);
        String userNameFromToken = jwtUtil.extractUsername(jwt); // jwtUtil은 JWT 유틸리티 클래스

        // 토큰 유효성 검사
        if (!jwtUtil.validateToken(jwt)) {
            throw new RuntimeException("Invalid or expired token");
        }
        // userId 및 roles 추출
        Long userId = jwtUtil.extractUserId(jwt);
        String role = jwtUtil.extractRoles(jwt);

        List<ChatRoom> chatRooms;
        if (role.equals("ROLE_ADMIN")) {  // 관리자인지 확인
            chatRooms = chatRoomService.getAllChatRooms();
        } else {
            chatRooms = chatRoomService.getUserChatRooms(userId);
            log.info("채팅방 조회: "+ userId);
        }
        return ResponseEntity.ok(chatRooms);
    }

    //채팅방 생성
    @PostMapping("/createRoom")
    public ResponseEntity<ChatRoom> createChatRoom(@RequestHeader("Authorization") String token){
        // JWT 토큰에서 Bearer 제거
        String jwt = token.substring(7);
        // userId 및 roles 추출
        Long userId = jwtUtil.extractUserId(jwt);
        Long adminId = userService.findAdminId();

        ChatRoom ChatRoom = chatRoomService.createRoom(adminId, userId);

        return ResponseEntity.ok(ChatRoom);
    }


    // 특정 채팅방 조회
    @GetMapping("/room/{RoomId}")
    @ResponseBody
    public Optional<ChatRoom> getRoom(@PathVariable String RoomId) {
        return chatRoomService.findByRoomId(RoomId);
    }

}