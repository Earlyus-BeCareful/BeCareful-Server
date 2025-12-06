package com.becareful.becarefulserver.domain.chat.dto.request;

import com.becareful.becarefulserver.domain.chat.domain.vo.ChatSendRequestType;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "sendRequestType", visible = true)
@JsonSubTypes({
    @JsonSubTypes.Type(value = SendTextChatRequest.class, name = "SEND_TEXT"),
    @JsonSubTypes.Type(value = EditContractChatRequest.class, name = "EDIT_CONTRACT"),
    @JsonSubTypes.Type(value = AcceptContractChatRequest.class, name = "ACCEPT_CONTRACT"),
    @JsonSubTypes.Type(value = ConfirmContractChatRequest.class, name = "CONFIRM_MATCHING")
})
public sealed interface ChatSendRequest
        permits AcceptContractChatRequest, ConfirmContractChatRequest, EditContractChatRequest, SendTextChatRequest {
    ChatSendRequestType sendRequestType();
}
