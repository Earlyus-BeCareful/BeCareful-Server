package com.becareful.becarefulserver.global.websocket;

import com.becareful.becarefulserver.domain.caregiver.repository.*;
import com.becareful.becarefulserver.domain.chat.domain.vo.*;
import com.becareful.becarefulserver.domain.socialworker.repository.SocialWorkerRepository;
import com.becareful.becarefulserver.global.util.*;
import jakarta.servlet.http.*;
import java.util.*;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.server.*;
import org.springframework.stereotype.*;
import org.springframework.web.socket.*;
import org.springframework.web.socket.server.*;
import org.springframework.web.util.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtHandshakeInterceptor implements HandshakeInterceptor {
    private final JwtUtil jwtUtil;
    private final CaregiverRepository caregiverRepository;
    private final SocialWorkerRepository socialWorkerRepository;

    @Override
    public boolean beforeHandshake(
            @NotNull ServerHttpRequest request,
            @NotNull ServerHttpResponse response,
            @NotNull WebSocketHandler wsHandler,
            @NotNull Map<String, Object> attributes) {
        if (request instanceof ServletServerHttpRequest serverHttpRequest) {
            log.info("웹소켓 handShake 인터셉터 시작");
            HttpServletRequest httpServletRequest = serverHttpRequest.getServletRequest();

            Cookie cookie = WebUtils.getCookie(httpServletRequest, "AccessToken");

            if (cookie == null || !jwtUtil.isValid(cookie.getValue())) {
                throw new HandshakeFailureException("웹소켓 handShake 인터셉터 실패. AccessToken 필요"); // TODO: 예외 처리
            }

            String phoneNumber = jwtUtil.getPhoneNumber(cookie.getValue());

            ChatSenderType type;
            if (caregiverRepository.existsByPhoneNumber(phoneNumber)) {
                type = ChatSenderType.CAREGIVER;
            } else if (socialWorkerRepository.existsByPhoneNumber(phoneNumber)) {
                type = ChatSenderType.SOCIAL_WORKER;
            } else throw new HandshakeFailureException("웹소켓 handShake 인터셉터 실패. 전화번호와 일치하는 사용자가 없음.");

            attributes.put("principal", new ChatPrincipal(type));
            log.info("웹소켓 handShake 인터셉터 통과");
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
