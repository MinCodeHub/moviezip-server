package com.example.moviezip.domain.chat;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@Document(collection = "ChatMessages")
public class ChatMessage {

    public enum MessageType {
        ENTER, CHAT, LEAVE
    }

    @Id
    private String id;

    private MessageType type;  // Enum을 String으로 저장
    private Long userId; // 보낸 사람의 ID (User 참조 대신)
    private String sender; // 보낸 사람 닉네임
    private String content; // 메시지 내용
    private LocalDateTime timestamp; // 메시지 생성 시간
    private String chatRoomId; // 채팅방 ID (ManyToOne 대신 ID 참조)

    public ChatMessage(MessageType type, Long userId, String sender, String content, String chatRoomId) {
        this.type = type;
        this.userId = userId;
        this.sender = sender;
        this.content = content;
        this.timestamp = LocalDateTime.now();
        this.chatRoomId = chatRoomId;
    }
}
