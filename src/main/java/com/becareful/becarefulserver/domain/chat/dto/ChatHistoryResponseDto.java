package com.becareful.becarefulserver.domain.chat.dto;

import com.becareful.becarefulserver.domain.chat.domain.vo.*;

public sealed interface ChatHistoryResponseDto permits TextChatHistoryResponseDto, ContractChatHistoryResponseDto {
    long chatId();

    ChatReceiveType chatType();

    ChatSenderType senderType();

    String sentTime();
}
