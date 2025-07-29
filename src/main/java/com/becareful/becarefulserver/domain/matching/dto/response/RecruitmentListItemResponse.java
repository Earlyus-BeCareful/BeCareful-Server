package com.becareful.becarefulserver.domain.matching.dto.response;

import com.becareful.becarefulserver.domain.matching.domain.Matching;
import com.becareful.becarefulserver.domain.matching.domain.Recruitment;
import com.becareful.becarefulserver.domain.matching.domain.vo.MatchingResultStatus;
import com.becareful.becarefulserver.domain.matching.dto.RecruitmentDto;
import com.becareful.becarefulserver.domain.matching.dto.RecruitmentSimpleDto;

public record RecruitmentListItemResponse(
        RecruitmentSimpleDto recruitmentInfo,
        MatchingResultStatus matchingResultStatus,
        boolean isHotRecruitment,
        boolean isHourlySalaryTop) {

    public static RecruitmentListItemResponse from(Matching matching) {
        Recruitment recruitment = matching.getRecruitment();
        return new RecruitmentListItemResponse(
                RecruitmentSimpleDto.from(recruitment),
                matching.getCaregiverMatchingResultInfo().judgeMatchingResultStatus(),
                // TODO : 매칭 필터 정보 추가
                false,
                false);
    }
}
