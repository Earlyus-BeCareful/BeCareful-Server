package com.becareful.becarefulserver.domain.chat.dto.response;

import com.becareful.becarefulserver.domain.chat.domain.vo.*;

public sealed interface ChatHistoryResponseDto permits TextChatHistoryResponseDto, ContractChatHistoryResponseDto {
    long chatId();

    ChatType chatType();

    ChatSenderType senderType();

    String sentTime();
}
