package com.example.moviezip.domain.chat;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Data
@Getter
@Setter
public class ReadMessagesDto {

    private Long userId;
    private List<String> messageIds;
    private String roomId; // 필요하다면
}
