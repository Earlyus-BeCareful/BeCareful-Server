package com.becareful.becarefulserver.domain.matching.dto.response;

import com.becareful.becarefulserver.domain.matching.domain.Matching;
import java.time.LocalDate;

public record CaregiverAppliedMatchingDetailResponse(
        RecruitmentDetailResponse recruitmentDetailInfo, LocalDate applyDate) {

    public static CaregiverAppliedMatchingDetailResponse of(
            Matching matching, boolean isHotRecruitment, boolean isHourlySalaryTop) {
        return new CaregiverAppliedMatchingDetailResponse(
                RecruitmentDetailResponse.from(matching, isHotRecruitment, isHourlySalaryTop),
                matching.getApplicationDate());
    }
}
