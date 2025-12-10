package com.becareful.becarefulserver.domain.matching.dto.response;

import com.becareful.becarefulserver.domain.matching.domain.Recruitment;
import com.becareful.becarefulserver.domain.matching.domain.vo.MatchingResultStatus;
import com.becareful.becarefulserver.domain.matching.dto.RecruitmentDto;

public record RecruitmentDetailResponse(
        RecruitmentDto recruitmentInfo,
        MatchingResultStatus matchingResultStatus,
        boolean isHotRecruitment,
        boolean isHourlySalaryTop) {

    public static RecruitmentDetailResponse of(
            Recruitment recruitment,
            MatchingResultStatus matchingResultStatus,
            boolean isHotRecruitment,
            boolean isHourlySalaryTop) {
        return new RecruitmentDetailResponse(
                RecruitmentDto.from(recruitment), matchingResultStatus, isHotRecruitment, isHourlySalaryTop);
    }
}
