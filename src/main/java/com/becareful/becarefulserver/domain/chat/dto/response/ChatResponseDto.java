package com.becareful.becarefulserver.domain.chat.dto.response;

public sealed interface ChatResponseDto permits TextChatResponseDto, ContractChatResponseDto {
        long chatId();
        String sentTime();
}
