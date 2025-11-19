package com.becareful.becarefulserver.domain.matching.dto.response;

import com.becareful.becarefulserver.domain.caregiver.domain.WorkApplication;
import com.becareful.becarefulserver.domain.matching.domain.Recruitment;
import java.time.LocalDate;

public record CaregiverAppliedMatchingDetailResponse(
        RecruitmentDetailResponse recruitmentDetailInfo, LocalDate applyDate) {

    public static CaregiverAppliedMatchingDetailResponse of(
            WorkApplication workApplication,
            Recruitment recruitment,
            boolean isHotRecruitment,
            boolean isHourlySalaryTop,
            boolean hasNewChat) {
        return new CaregiverAppliedMatchingDetailResponse(
                RecruitmentDetailResponse.of(matching, isHotRecruitment, isHourlySalaryTop, hasNewChat),
                matching.getApplicationDate());
    }
}
