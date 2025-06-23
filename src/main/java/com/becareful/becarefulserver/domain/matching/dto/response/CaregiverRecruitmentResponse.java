package com.becareful.becarefulserver.domain.matching.dto.response;

import com.becareful.becarefulserver.domain.matching.domain.Matching;
import com.becareful.becarefulserver.domain.matching.domain.Recruitment;
import com.becareful.becarefulserver.domain.matching.domain.vo.MatchingResultStatus;
import com.becareful.becarefulserver.domain.matching.dto.RecruitmentDto;

public record CaregiverRecruitmentResponse(
        RecruitmentDto recruitmentInfo,
        MatchingResultStatus matchingResultStatus,
        boolean isHotRecruitment,
        boolean isHourlySalaryTop) {

    public static CaregiverRecruitmentResponse from(Matching matching) {
        Recruitment recruitment = matching.getRecruitment();
        return new CaregiverRecruitmentResponse(
                RecruitmentDto.from(recruitment),
                matching.getCaregiverMatchingResultInfo().judgeMatchingResultStatus(),
                false,
                false);
    }
}
