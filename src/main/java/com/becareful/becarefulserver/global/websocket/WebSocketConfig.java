package com.becareful.becarefulserver.global.websocket;

import org.springframework.messaging.simp.config.ChannelRegistration;

public class WebSocketConfig {
    private PrincipalAssignChannelInterceptor principalAssignChannelInterceptor;

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration){
        registration.interceptors(new PrincipalAssignChannelInterceptor());
    }
}
