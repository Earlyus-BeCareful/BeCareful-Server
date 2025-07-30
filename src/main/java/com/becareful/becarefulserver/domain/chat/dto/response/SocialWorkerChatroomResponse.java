package com.becareful.becarefulserver.domain.chat.dto.response;

import com.becareful.becarefulserver.domain.caregiver.domain.Caregiver;
import com.becareful.becarefulserver.domain.matching.domain.Contract;
import com.becareful.becarefulserver.domain.matching.domain.Matching;
import com.becareful.becarefulserver.domain.matching.dto.CaregiverSimpleDto;
import com.becareful.becarefulserver.domain.matching.dto.ElderlySimpleDto;
import com.becareful.becarefulserver.domain.socialworker.domain.Elderly;
import java.time.Duration;
import java.time.LocalDateTime;

public record SocialWorkerChatroomResponse(
        Long matchingId,
        Long recruitmentId,
        CaregiverSimpleDto caregiverInfo,
        String recentChat,
        String time,
        ElderlySimpleDto elderlyInfo,
        Integer unreadCount) {

    public static SocialWorkerChatroomResponse of(Matching matching, Contract contract, boolean isCompleted) {
        Caregiver caregiver = matching.getWorkApplication().getCaregiver();
        Elderly elderly = matching.getRecruitment().getElderly();
        String recentChat = isCompleted ? "최종 승인이 확정되었습니다!" : "합격 축하드립니다.";
        String timeDifference = getTimeDifferenceString(contract);

        return new SocialWorkerChatroomResponse(
                matching.getId(),
                matching.getRecruitment().getId(),
                CaregiverSimpleDto.from(caregiver),
                recentChat,
                timeDifference,
                ElderlySimpleDto.from(elderly),
                0 // TODO : unread count 구현
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
