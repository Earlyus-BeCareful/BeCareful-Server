package com.becareful.becarefulserver.domain.chat.dto;

import com.becareful.becarefulserver.domain.chat.domain.vo.ChatType;

public sealed interface ChatResponseDto permits TextChatDto, ContractDto(
        Long id,
        ChatType getChatType,


) {
}
