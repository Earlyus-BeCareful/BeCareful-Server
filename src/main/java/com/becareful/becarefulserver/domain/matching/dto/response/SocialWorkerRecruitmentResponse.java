package com.becareful.becarefulserver.domain.matching.dto.response;

import com.becareful.becarefulserver.domain.matching.domain.Recruitment;
import com.becareful.becarefulserver.domain.matching.dto.ElderlySimpleDto;
import com.becareful.becarefulserver.domain.matching.dto.RecruitmentDto;
import com.becareful.becarefulserver.domain.socialworker.domain.Elderly;

public record SocialWorkerRecruitmentResponse(
        RecruitmentDto recruitmentInfo, ElderlySimpleDto elderlyInfo, long matchingCount, long applyCount) {

    // RecruitmentRepository 에서 프로젝션에 사용
    public SocialWorkerRecruitmentResponse(
            Recruitment recruitment, Elderly elderly, long matchingCount, long applyCount) {
        this(RecruitmentDto.from(recruitment), ElderlySimpleDto.from(elderly), matchingCount, applyCount);
    }
}
