package com.becareful.becarefulserver.domain.matching.dto.response;

import com.becareful.becarefulserver.domain.matching.domain.Recruitment;
import com.becareful.becarefulserver.domain.matching.dto.ElderlySimpleDto;
import com.becareful.becarefulserver.domain.matching.dto.RecruitmentDto;
import com.becareful.becarefulserver.domain.socialworker.domain.Elderly;

public record SocialWorkerRecruitmentResponse(
        RecruitmentDto recruitmentInfo, ElderlySimpleDto elderlyInfo, int matchingCount, int applyCount) {

    // RecruitmentRepository 에서 프로젝션에 사용
    public SocialWorkerRecruitmentResponse(
            Recruitment recruitment, Elderly elderly, int matchingCount, int applyCount) {
        this(RecruitmentDto.from(recruitment), ElderlySimpleDto.from(elderly), matchingCount, applyCount);
    }

    public static SocialWorkerRecruitmentResponse of(Recruitment recruitment, int matchingCount, int applyCount) {
        return new SocialWorkerRecruitmentResponse(
                RecruitmentDto.from(recruitment),
                ElderlySimpleDto.from(recruitment.getElderly()),
                matchingCount,
                applyCount);
    }
}
