package com.example.moviezip.controller.chat;

import com.example.moviezip.domain.CustomUserDetails;
import com.example.moviezip.domain.chat.ChatMessage;
import com.example.moviezip.domain.chat.ReadMessagesDto;
import com.example.moviezip.service.chat.ChatMessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
public class ChatMessageController {
    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;
    private final ChatMessageService chatMessageService;
    // 특정 채팅방의 메시지 목록 조회
    @GetMapping("chat/room/{roomId}/messages")
    public ResponseEntity<List<ChatMessage>> getMessagesByRoomId(@PathVariable String roomId) {
        List<ChatMessage> messages = chatMessageService.getMessagesByChatRoomId(roomId);
        log.debug("Messages fetched for roomId {}: {}", roomId, messages);
        return ResponseEntity.ok(messages);
    }

    @MessageMapping("/send/message")
    public void  sendMessage(@Payload ChatMessage message, @AuthenticationPrincipal CustomUserDetails customUserDetails) {

        if (customUserDetails != null) {
            log.info("customUserDetails.. = " + customUserDetails.getAuthorities());
            message.setSender(message.getSender());
            message.setChatRoomId(message.getChatRoomId());
            chatMessageService.saveMessage(message);
        } else {
            // 인증되지 않은 경우 처리 (예: 메시지 전송 불가)
            message.setSender("이용자"); // 예시로 "anonymous" 설정
        }
        // 동적으로 토픽으로 메시지 보내기
        simpMessagingTemplate.convertAndSend("/topic/chat/" + message.getChatRoomId(), message);
    }

    @MessageMapping("chat.enter.{roomId}")
    public void  enterRoom(@DestinationVariable String roomId, @Payload ChatMessage message) {
            message.setType(ChatMessage.MessageType.ENTER);
            message.setTimestamp(LocalDateTime.now());
            message.setChatRoomId(roomId);
            message.setContent(message.getSender() + "님이 입장하셨습니다.");
            chatMessageService.saveMessage(message);
            // 동적으로 토픽으로 메시지 보내기
            simpMessagingTemplate.convertAndSend("/topic/chat/" + roomId, message);
    }
    @MessageMapping("/chat/read")
    public void handleReadMessage(ReadMessagesDto readMessagesDto) {
        Long userId = readMessagesDto.getUserId();
        for (String messageId : readMessagesDto.getMessageIds()) {
            chatMessageService.markMessageAsRead(messageId, userId);

            //읽음 처리 후 상태 업데이트
            chatMessageService.getMessageById(messageId).ifPresent(updatedMessage -> {
                simpMessagingTemplate.convertAndSend(
                        "/topic/chat/" + updatedMessage.getChatRoomId(),
                        updatedMessage
                );
            });
        }
    }

}
