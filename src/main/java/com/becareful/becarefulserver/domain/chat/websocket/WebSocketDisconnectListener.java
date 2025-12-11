package com.becareful.becarefulserver.domain.chat.websocket;

import com.becareful.becarefulserver.domain.chat.service.CaregiverChatService;
import com.becareful.becarefulserver.domain.chat.service.SocialWorkerChatService;
import java.security.Principal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

@Slf4j
@Component
@RequiredArgsConstructor
public class WebSocketDisconnectListener implements ApplicationListener<SessionDisconnectEvent> {
    private final ChatSessionStore sessionStore;
    private final SocialWorkerChatService socialWorkerChatService;
    private final CaregiverChatService caregiverChatService;

    @Override
    public void onApplicationEvent(SessionDisconnectEvent event) {
        log.info("DisconnectListener 시작, sessionId={}", event.getSessionId());

        String sessionId = event.getSessionId();
        Principal principal = event.getUser();

        Long roomId = sessionStore.getRoomBySession(sessionId);

        if (principal == null) {
            log.warn("Disconnect: principal is null for sessionId={}, roomId={}", sessionId, roomId);
            if (roomId != null) sessionStore.removeSession(sessionId);
            return;
        }

        if (!(principal instanceof ChatPrincipal p)) {
            log.warn(
                    "Disconnect: principal is not ChatPrincipal (class={}, name={})",
                    principal.getClass(),
                    principal.getName());
            if (roomId != null) sessionStore.removeSession(sessionId);
            return;
        }

        if (roomId == null) {
            log.info("Disconnect: no room stored for sessionId={}", sessionId);
            sessionStore.removeSession(sessionId);
            sessionStore.removeUser(p.userId());
            return;
        }

        try {
            switch (p.senderType()) {
                case SOCIAL_WORKER -> socialWorkerChatService.leaveRoom(p.userId(), roomId);
                case CAREGIVER -> caregiverChatService.leaveRoom(roomId);
                default -> log.error("Disconnect: unexpected senderType={} for principal={}", p.senderType(), p);
            }
        } catch (Exception e) {
            log.error("Disconnect: failed to update lastReadAt for sessionId={}, roomId={}", sessionId, roomId, e);
            // 고려할 사항: 실패시 retry queue 등으로 넘길 수 있음
        } finally {
            sessionStore.removeSession(sessionId);
            sessionStore.removeUser(p.userId());
        }

        log.info("DisconnectListener 종료, sessionId={}", sessionId);
    }
}
