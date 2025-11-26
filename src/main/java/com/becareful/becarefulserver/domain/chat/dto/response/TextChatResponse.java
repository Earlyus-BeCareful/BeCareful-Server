package com.becareful.becarefulserver.domain.chat.dto.response;

import com.becareful.becarefulserver.domain.chat.domain.*;
import com.becareful.becarefulserver.domain.chat.domain.vo.*;

public record TextChatResponse(
        long ChatId, ChatReceiveType chatType, ChatSenderType senderType, String text, String sentTime)
        implements ChatResponse {
    public static TextChatResponse from(TextChat textChat) {
        return new TextChatResponse(
                textChat.getId(),
                ChatReceiveType.TEXT,
                textChat.getSenderType(),
                textChat.getText(),
                textChat.getCreateDate().toString());
    }
}
