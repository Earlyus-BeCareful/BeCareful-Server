package com.becareful.becarefulserver.domain.chat.dto.request;

import com.becareful.becarefulserver.domain.chat.domain.vo.ChatSendRequestType;
import com.fasterxml.jackson.annotation.JsonTypeName;

@JsonTypeName("CONFIRM_MATCHING")
public record ConfirmContractChatRequest(ChatSendRequestType sendRequestType, long lastContractChatId)
        implements ChatSendRequest {}
