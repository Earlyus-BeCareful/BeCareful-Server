package com.becareful.becarefulserver.global.util;

import java.time.Duration;
import java.time.LocalDateTime;

public class ChatUtil {

    public static String convertChatRoomListLastSendTimeFormat(LocalDateTime sendTime) {
        Duration duration = Duration.between(sendTime, LocalDateTime.now());

        long hours = duration.toHours();
        long minutes = duration.toMinutes();
        long days = duration.toDays();

        if (hours < 1) return minutes + "분 전";
        if (days < 1) return hours + "시간 전";
        return days + "일 전";
    }
}
