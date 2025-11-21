package com.becareful.becarefulserver.domain.chat.dto.request;

import com.becareful.becarefulserver.domain.chat.domain.vo.ChatSendRequestType;

public record TextChatSendRequest(
        ChatSendRequestType sendRequestType,
        String text
) implements ChatSendRequest{}
