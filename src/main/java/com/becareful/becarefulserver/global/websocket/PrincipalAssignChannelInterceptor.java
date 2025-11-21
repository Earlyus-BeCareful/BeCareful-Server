package com.becareful.becarefulserver.global.websocket;

import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;

public class PrincipalAssignChannelInterceptor implements ChannelInterceptor {
    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel){
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);

        if(accessor.getUser()==null){
            ChatPrincipal p = (ChatPrincipal) accessor.getSessionAttributes().get("PRINCIPAL");
            if(p!= null) accessor.setUser(p);
        }
        return message;
    }
}
