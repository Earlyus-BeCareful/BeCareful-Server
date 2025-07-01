package com.becareful.becarefulserver.domain.matching.dto.response;

import com.becareful.becarefulserver.domain.matching.domain.Recruitment;
import com.becareful.becarefulserver.domain.matching.dto.CaregiverSimpleDto;
import com.becareful.becarefulserver.domain.matching.dto.RecruitmentSimpleDto;
import java.util.List;

public record MatchingStatusDetailResponse(
        RecruitmentSimpleDto recruitmentInfo,
        List<CaregiverSimpleDto> matchedCaregivers,
        List<CaregiverSimpleDto> appliedCaregivers) {

    public static MatchingStatusDetailResponse of(
            Recruitment recruitment,
            List<CaregiverSimpleDto> matchedCaregivers,
            List<CaregiverSimpleDto> appliedCaregivers) {
        return new MatchingStatusDetailResponse(
                RecruitmentSimpleDto.from(recruitment), matchedCaregivers, appliedCaregivers);
    }
}
