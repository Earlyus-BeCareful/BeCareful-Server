package com.becareful.becarefulserver.global.websocket;

import lombok.*;
import org.springframework.context.annotation.*;
import org.springframework.messaging.simp.config.*;
import org.springframework.web.socket.config.annotation.*;

@RequiredArgsConstructor
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
    private final PrincipalAssignChannelInterceptor principalAssignChannelInterceptor;
    private final JwtHandshakeInterceptor jwtHandshakeInterceptor;

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws/chat")
                .addInterceptors(jwtHandshakeInterceptor)
                .setAllowedOrigins("*")
                .withSockJS();
        // SockJS를 안 쓰면 .withSockJS() 지워도 됨
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.setApplicationDestinationPrefixes("/app"); // 프론트에서 "/app/xxx"로 보내면 @MessageMapping에서 받음
        config.enableSimpleBroker("/topic"); // 서버가 "/topic/xxx"으로 브로드캐스트함
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(principalAssignChannelInterceptor);
    }
}
