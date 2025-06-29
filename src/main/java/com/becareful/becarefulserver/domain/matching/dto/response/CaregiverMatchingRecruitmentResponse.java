package com.becareful.becarefulserver.domain.matching.dto.response;

import com.becareful.becarefulserver.domain.matching.domain.Matching;
import com.becareful.becarefulserver.domain.matching.domain.Recruitment;
import com.becareful.becarefulserver.domain.matching.domain.vo.MatchingResultStatus;
import com.becareful.becarefulserver.domain.matching.dto.RecruitmentDto;

public record CaregiverMatchingRecruitmentResponse(
        RecruitmentDto recruitmentInfo,
        MatchingResultStatus matchingResultStatus,
        boolean isHotRecruitment,
        boolean isHourlySalaryTop) {

    public static CaregiverMatchingRecruitmentResponse from(Matching matching) {
        Recruitment recruitment = matching.getRecruitment();
        return new CaregiverMatchingRecruitmentResponse(
                RecruitmentDto.from(recruitment),
                matching.getCaregiverMatchingResultInfo().judgeMatchingResultStatus(),
                // TODO : 매칭 필터 정보 추가
                false,
                false);
    }
}
