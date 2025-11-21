package com.becareful.becarefulserver.global.websocket;

import com.becareful.becarefulserver.domain.caregiver.repository.CaregiverRepository;
import com.becareful.becarefulserver.domain.chat.domain.vo.ChatSenderType;
import com.becareful.becarefulserver.domain.socialworker.repository.SocialWorkerRepository;
import com.becareful.becarefulserver.global.util.AuthUtil;
import com.becareful.becarefulserver.global.util.JwtUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.util.WebUtils;

import java.util.Map;

@RequiredArgsConstructor
public class JwtHandshakeInterceptor implements HandlerInterceptor {
    private final JwtUtil jwtUtil;
    private final AuthUtil authUtil;
    private final CaregiverRepository caregiverRepository;
    private final SocialWorkerRepository socialworkerRepository;

    @Override
    public boolean beforeHandshake(ServerHttpRequest request,
                                   ServerHttpResponse response,
                                   WebSocketHandler wsHandler,
                                   Map<String, Object> attributes) throws Exception{
        if (request instanceof ServletServerHttpRequest serverHttpRequest) {
            HttpServletRequest httpServletRequest = serverHttpRequest.getServletRequest();

            Cookie cookie = WebUtils.getCookie(httpServletRequest, "Access_Token"); //TODO: 토큰 키 이름 확인

            if(cookie == null || !jwtUtil.isValid(cookie.getValue())) {
                throw new Exception("Invalid Access Token"); //TODO: 예외 처리
            }

            String phoneNumber = jwtUtil.getPhoneNumber(cookie.getValue());

            attributes.put("senderType", authUtil.getLoggedInChatSenderType());


            ChatSenderType type;
            if (caregiverRepository.existsByPhoneNumber(phoneNumber)) {
                type = ChatSenderType.CAREGIVER;
            } else {
                type = ChatSenderType.SOCIAL_WORKER;
            }

            attributes.put("PRINCIPAL",
                    new ChatPrincipal(type)
            );

        }
        return true;
    }
}
