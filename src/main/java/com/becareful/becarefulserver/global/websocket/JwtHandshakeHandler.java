package com.becareful.becarefulserver.global.websocket;

import java.security.Principal;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeFailureException;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

@Slf4j
@Component
public class JwtHandshakeHandler extends DefaultHandshakeHandler {
    @Override
    protected Principal determineUser(
            @NotNull ServerHttpRequest request, @NotNull WebSocketHandler wsHandler, Map<String, Object> attributes) {
        log.info("웹소켓 handshakerHandler 시작"); // TODO(에러 확인용. 삭제 예정)

        Object principal = attributes.get("principal");

        if (principal instanceof ChatPrincipal P) {
            log.info("웹소켓 handshakeHandler 통과"); // TODO(에러 확인용. 삭제 예정)
            return P;
        }
        throw new HandshakeFailureException("웹소켓 handshakeHandler 실패. 잘못된 principal 형식 입니다.");
    }
}
