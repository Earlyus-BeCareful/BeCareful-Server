package com.becareful.becarefulserver.domain.chat.dto.response;

import com.becareful.becarefulserver.domain.chat.domain.*;
import com.becareful.becarefulserver.domain.chat.domain.vo.ChatSenderType;

public record TextChatResponseDto(long chatId, ChatSenderType senderType, String sentTime, String text)
        implements ChatResponseDto {
    public static TextChatResponseDto from(TextChat textChat, String formattedTimeAgo) {
        return new TextChatResponseDto(
                textChat.getId(), textChat.getSenderType(), formattedTimeAgo, textChat.getText());
    }
}
