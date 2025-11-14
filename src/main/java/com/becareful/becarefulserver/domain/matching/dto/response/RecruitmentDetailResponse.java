package com.becareful.becarefulserver.domain.matching.dto.response;

import com.becareful.becarefulserver.domain.matching.domain.Matching;
import com.becareful.becarefulserver.domain.matching.domain.Recruitment;
import com.becareful.becarefulserver.domain.matching.domain.vo.MatchingResultStatus;
import com.becareful.becarefulserver.domain.matching.dto.RecruitmentDto;

public record RecruitmentDetailResponse(
        RecruitmentDto recruitmentInfo,
        MatchingResultStatus matchingResultStatus,
        boolean isHotRecruitment,
        boolean isHourlySalaryTop,
        boolean hasNewChat) {

    public static RecruitmentDetailResponse from(
            Matching matching, boolean isHotRecruitment, boolean isHourlySalaryTop, boolean hasNewChat) {
        Recruitment recruitment = matching.getRecruitment();
        return new RecruitmentDetailResponse(
                RecruitmentDto.from(recruitment),
                matching.getMatchingResultStatus(),
                isHotRecruitment,
                isHourlySalaryTop,
                hasNewChat);
    }
}
