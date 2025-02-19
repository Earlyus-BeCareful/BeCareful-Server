package com.becareful.becarefulserver.domain.recruitment.dto.response;

import com.becareful.becarefulserver.domain.common.domain.CareType;
import com.becareful.becarefulserver.domain.common.vo.Gender;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.EnumSet;
import java.util.List;

public record RecruitmentMatchingStateResponse(
        String elderlyName,
        EnumSet<CareType> careType,
        int elderlyAge,
        Gender gender,
        EnumSet<DayOfWeek> workDays,
        LocalTime workStartTime,
        LocalTime workEndTime,
        List<CaregiverDetail> unAppliedCaregivers,
        List<CaregiverDetail> appliedCaregivers
) {
    public record CaregiverDetail(
            Long caregiverId,
            String profileImageUrl,
            String name,
            String resumeTitle
    ) {}
}
