package com.becareful.becarefulserver.global.websocket;

import java.security.Principal;
import java.util.Map;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

@Component
public class JwtHandshakeHandler extends DefaultHandshakeHandler {
    @Override
    protected Principal determineUser(
            @NotNull ServerHttpRequest request, @NotNull WebSocketHandler wsHandler, Map<String, Object> attributes) {
        Object principal = attributes.get("PRINCIPAL");

        if (principal instanceof Principal P) {
            return P;
        }
        return null;
    }
}
