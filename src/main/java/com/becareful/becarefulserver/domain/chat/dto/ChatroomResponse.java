package com.becareful.becarefulserver.domain.chat.dto;

import com.becareful.becarefulserver.domain.matching.domain.Contract;
import com.becareful.becarefulserver.domain.matching.domain.Matching;
import com.becareful.becarefulserver.domain.nursing_institution.domain.NursingInstitution;
import java.time.Duration;
import java.time.LocalDateTime;

public record ChatroomResponse(
        Long matchingId,
        String nursingInstitutionProfileImageUrl,
        String nursingInstitutionName,
        String recentChat,
        String lastSendTime,
        Integer unreadCount) {

    public static ChatroomResponse of(Matching matching, Contract contract, boolean isCompleted) {
        NursingInstitution institution = matching.getRecruitment().getElderly().getNursingInstitution();
        String recentChat = isCompleted ? "최종 승인이 확정되었습니다!" : "합격 축하드립니다.";
        String timeDifference = getTimeDifferenceString(contract);
        return new ChatroomResponse(
                matching.getId(),
                institution.getProfileImageUrl(),
                institution.getName(),
                recentChat,
                timeDifference,
                0 // TODO : 안 읽은 메세지 카운팅 로직 구현
                );
    }

    private static String getTimeDifferenceString(Contract contract) {
        // 현재 시간
        LocalDateTime currentTime = LocalDateTime.now();

        // 가장 최신 Contract의 생성 시간
        LocalDateTime contractCreatedTime = contract.getCreateDate();

        // Duration을 사용하여 차이 계산
        Duration duration = Duration.between(contractCreatedTime, currentTime);

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
