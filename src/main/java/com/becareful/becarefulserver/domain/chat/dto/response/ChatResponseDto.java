package com.becareful.becarefulserver.domain.chat.dto.response;

import com.becareful.becarefulserver.domain.chat.domain.vo.ChatSenderType;

public sealed interface ChatResponseDto permits TextChatResponseDto, ContractChatResponseDto {
    long chatId();

    ChatSenderType senderType();

    String sentTime();
}
