package com.becareful.becarefulserver.domain.matching.dto.response;

import com.becareful.becarefulserver.domain.matching.domain.Application;
import com.becareful.becarefulserver.domain.matching.domain.vo.MatchingResultStatus;
import java.time.LocalDate;

public record CaregiverAppliedMatchingDetailResponse(
        RecruitmentDetailResponse recruitmentDetailInfo, LocalDate applyDate) {

    public static CaregiverAppliedMatchingDetailResponse of(
            Application application, MatchingResultStatus result, boolean isHotRecruitment, boolean isHourlySalaryTop) {
        return new CaregiverAppliedMatchingDetailResponse(
                RecruitmentDetailResponse.of(application.getRecruitment(), result, isHotRecruitment, isHourlySalaryTop),
                application.getCreateDate().toLocalDate());
    }
}
