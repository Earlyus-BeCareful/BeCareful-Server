package com.becareful.becarefulserver.domain.chat.dto.response;

import com.becareful.becarefulserver.domain.chat.domain.*;

public record TextChatResponseDto(long chatId, String sentTime, String text) implements ChatResponseDto {
    public static TextChatResponseDto from(TextChat textChat, String formattedTimeAgo) {
        return new TextChatResponseDto(textChat.getId(), formattedTimeAgo, textChat.getText());
    }
}
