package com.becareful.becarefulserver.domain.chat.dto.request;

import com.becareful.becarefulserver.domain.chat.domain.vo.ChatSendRequestType;
import com.becareful.becarefulserver.domain.chat.domain.vo.ChatSenderType;

public record ConfirmMatchingRequest(
        ChatSendRequestType sendRequestType,
        long lastContractChatId
) implements ChatSendRequest{
}
