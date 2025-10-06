package com.becareful.becarefulserver.domain.matching.dto.response;

import com.becareful.becarefulserver.domain.matching.domain.Recruitment;
import com.becareful.becarefulserver.domain.matching.dto.ElderlySimpleDto;
import com.becareful.becarefulserver.domain.matching.dto.RecruitmentDto;

public record RecruitmentListResponse(
        RecruitmentDto recruitmentInfo, ElderlySimpleDto elderlyInfo, int matchingCount, int applyCount) {
    public static RecruitmentListResponse of(Recruitment recruitment, int matchingCount, int applyCount) {
        return new RecruitmentListResponse(
                RecruitmentDto.from(recruitment),
                ElderlySimpleDto.from(recruitment.getElderly()),
                matchingCount,
                applyCount);
    }
}
