package com.becareful.becarefulserver.domain.chat.dto.response;

import com.becareful.becarefulserver.domain.chat.domain.vo.*;

public sealed interface ChatResponse
        permits TextChatResponse,
                ContractChatResponse,
                ChatRoomContractStatusUpdatedChatResponse,
                ChatRoomActiveStatusUpdatedChatResponse {
    ChatType chatType();
}
