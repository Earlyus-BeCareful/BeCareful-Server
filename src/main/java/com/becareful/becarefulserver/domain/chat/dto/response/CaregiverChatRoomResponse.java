package com.becareful.becarefulserver.domain.chat.dto.response;

import com.becareful.becarefulserver.domain.chat.domain.ChatMessage;
import com.becareful.becarefulserver.domain.chat.domain.ChatRoom;
import com.becareful.becarefulserver.domain.nursing_institution.domain.NursingInstitution;
import com.becareful.becarefulserver.domain.nursing_institution.dto.InstitutionSimpleDto;
import java.time.Duration;
import java.time.LocalDateTime;

public record CaregiverChatRoomResponse(
        Long chatRoomId,
        InstitutionSimpleDto institutionInfo,
        String recentChat,
        String lastSendTime,
        Integer unreadCount) {

    public static CaregiverChatRoomResponse of(ChatRoom chatRoom, ChatMessage recentMessage) {
        NursingInstitution institution =
                chatRoom.getMatching().getRecruitment().getElderly().getNursingInstitution();
        // String recentChat = isCompleted ? "최종 승인이 확정되었습니다!" : "합격 축하드립니다.";
        String timeDifference = getTimeDifferenceString(recentMessage.getCreateDate());
        return new CaregiverChatRoomResponse(
                chatRoom.getId(),
                InstitutionSimpleDto.from(institution),
                recentMessage.getContent(),
                timeDifference,
                0 // TODO : 안 읽은 메세지 카운팅 로직 구현
                );
    }

    private static String getTimeDifferenceString(LocalDateTime sendTime) {
        LocalDateTime currentTime = LocalDateTime.now();

        Duration duration = Duration.between(sendTime, currentTime);

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
