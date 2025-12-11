package com.becareful.becarefulserver.domain.chat.dto.request;

import com.becareful.becarefulserver.domain.chat.domain.vo.ChatSendRequestType;
import com.fasterxml.jackson.annotation.JsonTypeName;

@JsonTypeName("SEND_TEXT")
public record SendTextChatRequest(ChatSendRequestType sendRequestType, String text) implements ChatSendRequest {}
