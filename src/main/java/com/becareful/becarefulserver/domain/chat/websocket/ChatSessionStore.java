package com.becareful.becarefulserver.domain.chat.websocket;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Component;

@Component
public class ChatSessionStore {
    private final Map<String, Long> sessionToRoom = new ConcurrentHashMap<>();
    private final Map<String, Long> subscriptionToRoom = new ConcurrentHashMap<>();
    private final Map<Long, String> userToSession = new ConcurrentHashMap<>();

    public void setRoomForSession(String sessionId, Long roomId) {
        if (sessionId == null) return;
        if (roomId == null) {
            sessionToRoom.remove(sessionId);
        } else {
            sessionToRoom.put(sessionId, roomId);
        }
    }

    public Long getRoomBySession(String sessionId) {
        if (sessionId == null) return null;
        return sessionToRoom.get(sessionId);
    }

    public void removeSession(String sessionId) {
        sessionToRoom.remove(sessionId);
    }

    /* Subscription-based methods */
    public void setRoomForSubscription(String subscriptionId, Long roomId) {
        if (subscriptionId == null) return;
        if (roomId == null) {
            subscriptionToRoom.remove(subscriptionId);
        } else {
            subscriptionToRoom.put(subscriptionId, roomId);
        }
    }

    public Long getRoomBySubscription(String subscriptionId) {
        if (subscriptionId == null) return null;
        return subscriptionToRoom.get(subscriptionId);
    }

    public void removeSubscription(String subscriptionId) {
        if (subscriptionId == null) return;
        subscriptionToRoom.remove(subscriptionId);
    }

    /* User-session mapping (optional) */
    public void setSessionForUser(Long userId, String sessionId) {
        if (userId == null) return;
        if (sessionId == null) {
            userToSession.remove(userId);
        } else {
            userToSession.put(userId, sessionId);
        }
    }

    public String getSessionByUser(Long userId) {
        if (userId == null) return null;
        return userToSession.get(userId);
    }

    public void removeUser(Long userId) {
        if (userId == null) return;
        userToSession.remove(userId);
    }
}
