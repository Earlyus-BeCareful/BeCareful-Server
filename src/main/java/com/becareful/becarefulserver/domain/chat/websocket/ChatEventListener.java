package com.becareful.becarefulserver.domain.chat.websocket;

import com.becareful.becarefulserver.domain.chat.domain.vo.ChatSenderType;
import com.becareful.becarefulserver.domain.chat.service.CaregiverChatService;
import com.becareful.becarefulserver.domain.chat.service.SocialWorkerChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ChatEventListener {
    private final SocialWorkerChatService socialWorkerChatService;
    private final CaregiverChatService caregiverChatService;

    @Async
    @EventListener
    public void onChatEvent(ChatEvent event) {

        switch (event.eventType()) {
            case ENTER -> handleEnter(event);
            case LEAVE -> handleLeave(event);
            case DISCONNECT -> handleDisconnect(event);
        }
    }

    private void handleEnter(ChatEvent event) {
        log.info("ENTER event: room={}, user={}", event.roomId(), event.userId());
        // enter 시 특별히 할 작업 있으면 여기서
    }

    private void handleLeave(ChatEvent event) {
        log.info("LEAVE event: room={}, user={}", event.roomId(), event.userId());

        updateLastReadTime(event);
    }

    private void handleDisconnect(ChatEvent event) {
        log.info("DISCONNECT event: room={}, user={}", event.roomId(), event.userId());

        updateLastReadTime(event);
    }

    private void updateLastReadTime(ChatEvent event) {
        if (event.senderType() == ChatSenderType.SOCIAL_WORKER) {
            socialWorkerChatService.leaveRoom(event.userId(), event.roomId());
        } else if (event.senderType() == ChatSenderType.CAREGIVER) {
            caregiverChatService.leaveRoom(event.roomId());
        }
    }
}
