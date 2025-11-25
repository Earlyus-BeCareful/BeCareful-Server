package com.becareful.becarefulserver.domain.chat.dto;

import com.becareful.becarefulserver.domain.chat.domain.*;
import com.becareful.becarefulserver.domain.chat.domain.vo.*;
import com.fasterxml.jackson.annotation.*;
import java.time.*;

public record TextChatHistoryResponseDto(
        long chatId,
        ChatReceiveType chatType,
        ChatSenderType senderType,
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss") LocalDateTime sentTime,
        String text)
        implements ChatHistoryResponseDto {
    public static TextChatHistoryResponseDto from(TextChat textChat) {
        return new TextChatHistoryResponseDto(
                textChat.getId(),
                ChatReceiveType.TEXT,
                textChat.getSenderType(),
                textChat.getCreateDate(),
                textChat.getText());
    }
}
