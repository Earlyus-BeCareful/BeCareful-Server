package com.becareful.becarefulserver.domain.chat.dto.response;

import com.becareful.becarefulserver.domain.chat.domain.*;
import com.becareful.becarefulserver.domain.chat.domain.vo.*;

public record TextChatHistoryResponseDto(
        long chatId, ChatType chatType, ChatSenderType senderType, String sentTime, String text)
        implements ChatHistoryResponseDto {
    public static TextChatHistoryResponseDto from(TextChat textChat, String formattedTimeAgo) {
        return new TextChatHistoryResponseDto(
                textChat.getId(), ChatType.TEXT, textChat.getSenderType(), formattedTimeAgo, textChat.getText());
    }
}
