package com.becareful.becarefulserver.domain.chat.websocket;

import com.becareful.becarefulserver.domain.chat.domain.vo.ChatSenderType;

public record ChatEvent(Long roomId, Long userId, ChatSenderType senderType, ChatEventType eventType) {
    public static ChatEvent of(ChatPrincipal principal, Long roomId, ChatEventType eventType) {
        return new ChatEvent(roomId, principal.userId(), principal.senderType(), eventType);
    }

    public enum ChatEventType {
        ENTER,
        LEAVE,
        DISCONNECT
    }
}
