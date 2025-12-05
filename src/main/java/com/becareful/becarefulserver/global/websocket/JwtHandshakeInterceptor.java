package com.becareful.becarefulserver.global.websocket;

import com.becareful.becarefulserver.domain.caregiver.repository.*;
import com.becareful.becarefulserver.domain.chat.domain.vo.*;
import com.becareful.becarefulserver.global.util.*;
import jakarta.servlet.http.*;
import java.util.*;
import lombok.*;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.server.*;
import org.springframework.stereotype.*;
import org.springframework.web.socket.*;
import org.springframework.web.socket.server.*;
import org.springframework.web.util.*;

@Component
@RequiredArgsConstructor
public class JwtHandshakeInterceptor implements HandshakeInterceptor {
    private final JwtUtil jwtUtil;
    private final CaregiverRepository caregiverRepository;

    @Override
    public boolean beforeHandshake(
            @NotNull ServerHttpRequest request,
            @NotNull ServerHttpResponse response,
            @NotNull WebSocketHandler wsHandler,
            @NotNull Map<String, Object> attributes) {
        if (request instanceof ServletServerHttpRequest serverHttpRequest) {
            HttpServletRequest httpServletRequest = serverHttpRequest.getServletRequest();

            Cookie cookie = WebUtils.getCookie(httpServletRequest, "AccessToken");

            if (cookie == null || !jwtUtil.isValid(cookie.getValue())) {
                throw new HandshakeFailureException("웹소켓 연결에 실패했습니다. 재로그인 해주십시오."); // TODO: 예외 처리
            }

            String phoneNumber = jwtUtil.getPhoneNumber(cookie.getValue());

            ChatSenderType type = caregiverRepository.existsByPhoneNumber(phoneNumber)
                    ? ChatSenderType.CAREGIVER
                    : ChatSenderType.SOCIAL_WORKER;

            attributes.put("PRINCIPAL", new ChatPrincipal(type));
        }
        return true;
    }

    @Override
    public void afterHandshake(
            @NotNull ServerHttpRequest request,
            @NotNull ServerHttpResponse response,
            @NotNull WebSocketHandler wsHandler,
            Exception exception) {}
}
