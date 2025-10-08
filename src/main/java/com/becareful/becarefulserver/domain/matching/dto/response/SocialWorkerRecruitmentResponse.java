package com.becareful.becarefulserver.domain.matching.dto.response;

import com.becareful.becarefulserver.domain.matching.domain.Recruitment;
import com.becareful.becarefulserver.domain.matching.dto.ElderlySimpleDto;
import com.becareful.becarefulserver.domain.matching.dto.RecruitmentDto;

public record SocialWorkerRecruitmentResponse(
        RecruitmentDto recruitmentInfo, ElderlySimpleDto elderlyInfo, int matchingCount, int applyCount) {
    public static SocialWorkerRecruitmentResponse of(Recruitment recruitment, int matchingCount, int applyCount) {
        return new SocialWorkerRecruitmentResponse(
                RecruitmentDto.from(recruitment),
                ElderlySimpleDto.from(recruitment.getElderly()),
                matchingCount,
                applyCount);
    }
}
