package com.becareful.becarefulserver.domain.chat.websocket;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class StompChannelInterceptor implements ChannelInterceptor {

    private final ApplicationEventPublisher eventPublisher;
    private final ChatSessionStore sessionStore;

    @Override
    public Message<?> preSend(@NotNull Message<?> message, @NotNull MessageChannel channel) {

        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
        StompCommand command = accessor.getCommand();
        if (command == null) return message;

        try {
            if (StompCommand.SUBSCRIBE.equals(command)) {
                handleEnter(accessor);
            } else if (StompCommand.UNSUBSCRIBE.equals(command)) {
                handleLeave(accessor);
            } else if (StompCommand.DISCONNECT.equals(command)) {
                handleDisconnect(accessor);
            }
        } catch (Exception e) {
            log.error("StompChannelInterceptor 처리 중 예외", e);
            // 예외를 던지면 메시지 흐름이 막힐 수 있으므로 로깅 후 넘어감
        }

        return message;
    }

    private void handleEnter(StompHeaderAccessor accessor) {
        ChatPrincipal principal = (ChatPrincipal) accessor.getUser();
        if (principal == null) {
            log.warn("SUBSCRIBE: principal is null, sessionId={}", accessor.getSessionId());
            return;
        }

        String destination = accessor.getDestination();
        Long roomId = extractRoomId(destination);
        String subscriptionId = accessor.getSubscriptionId();
        String sessionId = accessor.getSessionId();

        log.info(
                "SUBSCRIBE: sessionId={}, subscriptionId={}, destination={}, roomId={}, userId={}",
                sessionId,
                subscriptionId,
                destination,
                roomId,
                principal.userId());

        if (roomId == null) {
            log.warn("SUBSCRIBE: destination에서 roomId 추출 실패, destination={}", destination);
            return;
        }

        // subscriptionId -> roomId 저장 (UNSUBSCRIBE 처리용)
        if (subscriptionId != null) {
            sessionStore.setRoomForSubscription(subscriptionId, roomId);
        }

        // 한 세션에 이미 다른 room이 있으면 이전 방에 LEAVE 이벤트 발행 (서버 정책: 세션당 1방)
        Long previousRoom = sessionStore.getRoomBySession(sessionId);
        if (previousRoom != null && !previousRoom.equals(roomId)) {
            log.info(
                    "SUBSCRIBE: same session에 다른 room이 존재함. 이전 roomId={} 에 LEAVE 발행(sessionId={})",
                    previousRoom,
                    sessionId);
            eventPublisher.publishEvent(new ChatEvent(
                    previousRoom, principal.userId(), principal.senderType(), ChatEvent.ChatEventType.LEAVE));
        }

        // sessionId -> roomId 저장
        sessionStore.setRoomForSession(sessionId, roomId);

        // userId -> sessionId 저장(선택적, 편의 조회)
        sessionStore.setSessionForUser(principal.userId(), sessionId);

        // ENTER 이벤트 발행
        eventPublisher.publishEvent(ChatEvent.of(principal, roomId, ChatEvent.ChatEventType.ENTER));
    }

    private void handleLeave(StompHeaderAccessor accessor) {
        ChatPrincipal principal = (ChatPrincipal) accessor.getUser();
        String subscriptionId = accessor.getSubscriptionId();
        String sessionId = accessor.getSessionId();

        log.info(
                "UNSUBSCRIBE: sessionId={}, subscriptionId={}, user={}",
                sessionId,
                subscriptionId,
                principal == null ? null : principal.userId());

        Long roomId = null;
        if (subscriptionId != null) {
            roomId = sessionStore.getRoomBySubscription(subscriptionId);
            sessionStore.removeSubscription(subscriptionId);
        }

        // fallback: subscriptionId로 못찾으면 session->room 조회
        if (roomId == null) {
            roomId = sessionStore.getRoomBySession(sessionId);
        }

        if (principal == null) {
            log.warn("UNSUBSCRIBE: principal is null, sessionId={}, roomId={}", sessionId, roomId);
            // 세션 매핑을 정리해주자
            if (roomId != null) sessionStore.removeSession(sessionId);
            return;
        }

        if (roomId == null) {
            log.info("UNSUBSCRIBE: roomId not found for sessionId={}, subscriptionId={}", sessionId, subscriptionId);
            return;
        }

        // LEAVE 이벤트 발행
        eventPublisher.publishEvent(
                new ChatEvent(roomId, principal.userId(), principal.senderType(), ChatEvent.ChatEventType.LEAVE));

        // 해당 세션의 room 제거 (UNSUBSCRIBE가 마지막 구독이라면)
        sessionStore.removeSession(sessionId);
        // user->session은 세션이 완전히 끊길 때만 삭제하도록 유지(Disconnect에서 제거)
    }

    private void handleDisconnect(StompHeaderAccessor accessor) {
        ChatPrincipal principal = (ChatPrincipal) accessor.getUser();
        String sessionId = accessor.getSessionId();

        log.info(
                "DISCONNECT(pre-send): sessionId={}, user={}",
                sessionId,
                principal == null ? null : principal.userId());

        if (principal == null) {
            // 세션 매핑만 정리
            Long rm = sessionStore.getRoomBySession(sessionId);
            if (rm != null) sessionStore.removeSession(sessionId);
            return;
        }

        Long roomId = sessionStore.getRoomBySession(sessionId);
        if (roomId == null) {
            log.info("DISCONNECT: session에 매핑된 room 없음, sessionId={}", sessionId);
            sessionStore.removeSession(sessionId);
            sessionStore.removeUser(principal.userId());
            return;
        }

        // DISCONNECT 이벤트 발행
        eventPublisher.publishEvent(
                new ChatEvent(roomId, principal.userId(), principal.senderType(), ChatEvent.ChatEventType.DISCONNECT));

        // 매핑 정리
        sessionStore.removeSession(sessionId);
        sessionStore.removeUser(principal.userId());
    }

    private Long extractRoomId(String destination) {
        if (destination == null) return null;
        // ex: /topic/chat-room/5
        String[] parts = destination.split("/");
        try {
            return Long.parseLong(parts[parts.length - 1]);
        } catch (NumberFormatException e) {
            log.warn("extractRoomId: destination 마지막 파트가 숫자가 아님 destination={}", destination);
            return null;
        }
    }
}
