package com.becareful.becarefulserver.domain.matching.dto.response;

import com.becareful.becarefulserver.domain.matching.domain.Application;
import java.time.LocalDate;

public record CaregiverAppliedMatchingDetailResponse(
        RecruitmentDetailResponse recruitmentDetailInfo, LocalDate applyDate) {

    public static CaregiverAppliedMatchingDetailResponse of(
            Application application, boolean isHotRecruitment, boolean isHourlySalaryTop, boolean hasNewChat) {
        return new CaregiverAppliedMatchingDetailResponse(
                RecruitmentDetailResponse.of(
                        application.getWorkApplication(),
                        application.getRecruitment(),
                        isHotRecruitment,
                        isHourlySalaryTop,
                        hasNewChat),
                application.getCreateDate().toLocalDate());
    }
}
