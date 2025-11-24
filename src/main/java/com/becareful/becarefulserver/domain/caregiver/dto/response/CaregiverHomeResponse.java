package com.becareful.becarefulserver.domain.caregiver.dto.response;

import com.becareful.becarefulserver.domain.caregiver.domain.Caregiver;
import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;

@Builder(access = AccessLevel.PRIVATE)
public record CaregiverHomeResponse(
        String name,
        boolean hasNewChat,
        Long applicationCount,
        Long recruitmentCount,
        List<WorkScheduleResponse> workScheduleList,
        boolean isWorking,
        boolean isApplying) {
    public static CaregiverHomeResponse of(
            Caregiver caregiver,
            boolean isNewChat,
            Long applicationCount,
            Long recruitmentCount,
            boolean isWorking,
            boolean isApplying,
            List<WorkScheduleResponse> workScheduleList) {
        return CaregiverHomeResponse.builder()
                .name(caregiver.getName())
                .hasNewChat(isNewChat)
                .applicationCount(applicationCount)
                .recruitmentCount(recruitmentCount)
                .workScheduleList(workScheduleList)
                .isWorking(isWorking)
                .isApplying(isApplying)
                .build();
    }
}
