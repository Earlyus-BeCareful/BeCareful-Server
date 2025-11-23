package com.becareful.becarefulserver.global.websocket;

import com.becareful.becarefulserver.domain.caregiver.repository.*;
import com.becareful.becarefulserver.domain.chat.domain.vo.*;
import com.becareful.becarefulserver.domain.socialworker.repository.*;
import com.becareful.becarefulserver.global.util.*;
import jakarta.servlet.http.*;
import java.util.*;
import lombok.*;
import org.springframework.http.server.*;
import org.springframework.web.socket.*;
import org.springframework.web.socket.server.*;
import org.springframework.web.util.*;

@RequiredArgsConstructor
public class JwtHandshakeInterceptor implements HandshakeInterceptor {
    private final JwtUtil jwtUtil;
    private final AuthUtil authUtil;
    private final CaregiverRepository caregiverRepository;
    private final SocialWorkerRepository socialworkerRepository;

    @Override
    public boolean beforeHandshake(
            ServerHttpRequest request,
            ServerHttpResponse response,
            WebSocketHandler wsHandler,
            Map<String, Object> attributes) {
        if (request instanceof ServletServerHttpRequest serverHttpRequest) {
            HttpServletRequest httpServletRequest = serverHttpRequest.getServletRequest();

            Cookie cookie = WebUtils.getCookie(httpServletRequest, "AccessToken");

            if (cookie == null || !jwtUtil.isValid(cookie.getValue())) {
                throw new HandshakeFailureException("웹소켓 연결에 실패했습니다. 재로그인 해주십시오."); // TODO: 예외 처리
            }

            String phoneNumber = jwtUtil.getPhoneNumber(cookie.getValue());

            ChatSenderType type;

            if (caregiverRepository.existsByPhoneNumber(phoneNumber)) {
                type = ChatSenderType.CAREGIVER;
            } else {
                type = ChatSenderType.SOCIAL_WORKER;
            }

            attributes.put("PRINCIPAL", new ChatPrincipal(type));
        }
        return true;
    }

    @Override
    public void afterHandshake(
            ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception exception) {}
}
