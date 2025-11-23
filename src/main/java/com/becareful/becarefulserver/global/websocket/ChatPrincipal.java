package com.becareful.becarefulserver.global.websocket;

import com.becareful.becarefulserver.domain.chat.domain.vo.ChatSenderType;
import java.security.Principal;

public record ChatPrincipal(ChatSenderType senderType) implements Principal {
    @Override
    public String getName() {
        return senderType.toString();
    }
}
