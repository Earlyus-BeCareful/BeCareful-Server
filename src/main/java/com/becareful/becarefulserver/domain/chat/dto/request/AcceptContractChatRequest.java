package com.becareful.becarefulserver.domain.chat.dto.request;

import com.becareful.becarefulserver.domain.chat.domain.vo.ChatSendRequestType;
import com.fasterxml.jackson.annotation.JsonTypeName;

@JsonTypeName("ACCEPT_CONTRACT")
public record AcceptContractChatRequest(ChatSendRequestType sendRequestType, long lastContractChatId)
        implements ChatSendRequest {}
