package com.example.moviezip.service.chat;

import com.example.moviezip.dao.mongo.chat.ChatMessageRepository;
import com.example.moviezip.domain.chat.ChatMessage;
import com.example.moviezip.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service

public class ChatMessageService {

    private final ChatMessageRepository chatMessageRepository;
    private final UserService userService;
    private final RedisTemplate<String, String> redisTemplate;


    public void saveMessage(ChatMessage message) {
        String roomId = message.getChatRoomId();

        //Redis에서 채팅방 유저 리스트 조회
        Set<String> userIds = redisTemplate.opsForSet().members("CHAT_ROOM:"+ roomId);
        List<Long> unreadUserIds = userIds.stream()
                .map(Long::parseLong)
                .filter(id -> !id.equals(message.getUserId()))//보낸 사람은 제외
                .collect(Collectors.toList());
        message.setUnreadUserIds(unreadUserIds);

        chatMessageRepository.save(message);
    }
    // 메시지를 읽었을 때 unreadUsersIds에서 제거
    public void markMessageAsRead(String messageId, Long userId) {
        Optional<ChatMessage> optionalMessage = chatMessageRepository.findById(messageId);

        optionalMessage.ifPresent(msg -> {
            msg.getUnreadUserIds().remove(userId);
            chatMessageRepository.save(msg);
        });
    }


    // 특정 채팅방의 메시지 목록 조회
    public List<ChatMessage> getMessagesByChatRoomId(String roomId) {
        return chatMessageRepository.findByChatRoomId(roomId);
    }

    // 읽지 않은 메시지 목록 조회
    public List<ChatMessage> getUnreadMessages(String roomId, Long userId) {
        return chatMessageRepository.findByChatRoomIdAndUnreadUserIdsContains(roomId, userId);
    }

    public Optional<ChatMessage> getMessageById(String messageId) {
        return chatMessageRepository.findById(messageId);
    }


}