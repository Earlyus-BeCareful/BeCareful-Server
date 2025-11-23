package com.becareful.becarefulserver.domain.chat.dto.request;

import com.becareful.becarefulserver.domain.chat.domain.vo.ChatSendRequestType;

public record SendTextChatRequest(ChatSendRequestType sendRequestType, String text) implements ChatSendRequest {}
