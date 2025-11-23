package com.becareful.becarefulserver.domain.chat.dto.request;

import com.becareful.becarefulserver.domain.chat.domain.vo.ChatSendRequestType;

public sealed interface ChatSendRequest
        permits AcceptContractChatRequest, ConfirmContractChatRequest, EditContractChatRequest, SendTextChatRequest {
    ChatSendRequestType sendRequestType();
}
