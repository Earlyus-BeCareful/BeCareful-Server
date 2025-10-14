package com.becareful.becarefulserver.global.util;

import java.time.Duration;
import java.time.LocalDateTime;

public class DateTimeUtil {

    public static String getTimeDifferenceString(LocalDateTime time) {
        // 현재 시간
        LocalDateTime currentTime = LocalDateTime.now();

        // Duration을 사용하여 차이 계산
        Duration duration = Duration.between(time, currentTime);

        // 차이에 따라 다른 시간 단위로 변환
        if (duration.toHours() < 1) {
            // 1시간 이내이면 분 단위로 반환
            long minutes = duration.toMinutes();
            return minutes + "분 전";
        } else if (duration.toDays() < 1) {
            // 1일 이내이면 시간 단위로 반환
            long hours = duration.toHours();
            return hours + "시간 전";
        } else {
            // 1일 이상이면 일 단위로 반환
            long days = duration.toDays();
            return days + "일 전";
        }
    }
}
