package com.becareful.becarefulserver.domain.chat.dto.request;

import com.becareful.becarefulserver.domain.chat.domain.vo.ChatSendRequestType;
import com.becareful.becarefulserver.domain.chat.domain.vo.ChatSenderType;

public record TextChatSendRequest(
        ChatSenderType senderType,
        ChatSendRequestType sendRequestType,
        String text
) implements ChatSendRequest{}
