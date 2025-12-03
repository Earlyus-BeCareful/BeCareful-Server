package com.becareful.becarefulserver.domain.chat.dto.request;

import com.becareful.becarefulserver.domain.chat.domain.vo.ChatSendRequestType;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "sendRequestType")
public sealed interface ChatSendRequest
        permits AcceptContractChatRequest, ConfirmContractChatRequest, EditContractChatRequest, SendTextChatRequest {
    ChatSendRequestType sendRequestType();
}
