package com.becareful.becarefulserver.domain.chat.websocket;

import lombok.*;
import org.springframework.context.annotation.*;
import org.springframework.messaging.simp.config.*;
import org.springframework.web.socket.config.annotation.*;

@RequiredArgsConstructor
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
    private final JwtHandshakeInterceptor jwtHandshakeInterceptor;
    private final JwtHandshakeHandler jwtHandshakeHandler;
    private final StompChannelInterceptor stompChannelInterceptor;

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws/chat")
                .setHandshakeHandler(jwtHandshakeHandler)
                .addInterceptors(jwtHandshakeInterceptor)
                .setAllowedOriginPatterns(
                        "https://becareful.vercel.app",
                        "https://www.carebridges.kr",
                        "https://localhost:5173",
                        "https://localhost:3000",
                        "https://be-careful-client-*.vercel.app");
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(stompChannelInterceptor);
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.setApplicationDestinationPrefixes("/app"); // 프론트에서 "/app/xxx"로 보내면 @MessageMapping에서 받음
        config.enableSimpleBroker("/topic"); // 서버가 "/topic/xxx"으로 브로드캐스트함
    }
}
