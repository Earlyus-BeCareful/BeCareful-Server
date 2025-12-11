package com.becareful.becarefulserver.domain.chat.dto.response;

import com.becareful.becarefulserver.domain.chat.domain.*;
import com.becareful.becarefulserver.domain.chat.domain.vo.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;

public record TextChatResponse(
        long ChatId,
        ChatReceiveType chatType,
        ChatSenderType senderType,
        String text,
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm") LocalDateTime sentTime)
        implements ChatResponse {
    public static TextChatResponse from(TextChat textChat) {
        return new TextChatResponse(
                textChat.getId(),
                ChatReceiveType.TEXT,
                textChat.getSenderType(),
                textChat.getText(),
                textChat.getCreateDate());
    }
}
