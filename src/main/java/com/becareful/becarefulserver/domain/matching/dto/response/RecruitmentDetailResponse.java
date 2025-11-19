package com.becareful.becarefulserver.domain.matching.dto.response;

import com.becareful.becarefulserver.domain.caregiver.domain.WorkApplication;
import com.becareful.becarefulserver.domain.matching.domain.Recruitment;
import com.becareful.becarefulserver.domain.matching.domain.vo.MatchingResultStatus;
import com.becareful.becarefulserver.domain.matching.dto.RecruitmentDto;
import com.becareful.becarefulserver.global.util.MatchingUtil;

public record RecruitmentDetailResponse(
        RecruitmentDto recruitmentInfo,
        MatchingResultStatus matchingResultStatus,
        boolean isHotRecruitment,
        boolean isHourlySalaryTop,
        boolean hasNewChat) {

    public static RecruitmentDetailResponse of(
            WorkApplication workApplication,
            Recruitment recruitment,
            boolean isHotRecruitment,
            boolean isHourlySalaryTop,
            boolean hasNewChat) {
        return new RecruitmentDetailResponse(
                RecruitmentDto.from(recruitment),
                MatchingUtil.calculateMatchingStatus(workApplication, recruitment),
                isHotRecruitment,
                isHourlySalaryTop,
                hasNewChat);
    }
}
