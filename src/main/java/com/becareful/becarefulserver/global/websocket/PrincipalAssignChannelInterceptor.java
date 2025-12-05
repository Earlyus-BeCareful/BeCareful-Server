package com.becareful.becarefulserver.global.websocket;

import java.util.*;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.*;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.*;

@Slf4j
@Component
public class PrincipalAssignChannelInterceptor implements ChannelInterceptor {
    @Override
    public Message<?> preSend(@NotNull Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);

        if (accessor.getUser() == null) {
            Map<String, Object> sessionAttributes = accessor.getSessionAttributes();

            if (sessionAttributes == null) {
                log.error("[preSend] SessionAttributes is null");
                throw new IllegalStateException("WebSocket 세션이 유효하지 않습니다.");
            }

            Object principal = sessionAttributes.get("PRINCIPAL");

            if (principal == null) {
                log.error("[preSend] PRINCIPAL is null");
                throw new IllegalStateException("인증 정보가 없습니다. 재로그인 해주세요.");
            }

            if (!(principal instanceof ChatPrincipal)) {
                log.error("Invalid PRINCIPAL type: {}", principal.getClass().getName());
                throw new IllegalStateException("인증 정보 형식이 올바르지 않습니다.");
            }

            accessor.setUser((ChatPrincipal) principal);
            log.debug("[preSend] Principal assigned");
        }
        return message;
    }
}
