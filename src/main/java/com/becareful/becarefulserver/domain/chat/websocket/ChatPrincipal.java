package com.becareful.becarefulserver.domain.chat.websocket;

import com.becareful.becarefulserver.domain.chat.domain.vo.ChatSenderType;
import java.security.Principal;

public record ChatPrincipal(ChatSenderType senderType, Long userId) implements Principal {
    @Override
    public String getName() {
        return senderType + ":" + userId;
    }
}
