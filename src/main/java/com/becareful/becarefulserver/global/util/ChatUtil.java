package com.becareful.becarefulserver.global.util;

import java.time.Duration;
import java.time.LocalDateTime;

public class ChatUtil {

    public static String convertChatRoomListLastSendTimeFormat(LocalDateTime sendTime) {
        Duration duration = Duration.between(sendTime, LocalDateTime.now());

        long days = duration.toDays();
        if (days > 0) {
            return days + "일 전";
        }
        long hours = duration.toHours();
        if (hours > 0) {
            return hours + "시간 전";
        }
        long minutes = duration.toMinutes();
        if (minutes > 0) {
            return minutes + "분 전";
        }
        return "방금 전";
    }
}
