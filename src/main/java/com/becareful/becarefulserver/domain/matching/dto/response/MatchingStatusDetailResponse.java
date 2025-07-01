package com.becareful.becarefulserver.domain.matching.dto.response;

import com.becareful.becarefulserver.domain.matching.domain.Recruitment;
import com.becareful.becarefulserver.domain.matching.dto.RecruitmentSimpleDto;
import java.util.List;

public record MatchingStatusDetailResponse(
        RecruitmentSimpleDto recruitmentInfo,
        List<MatchingCaregiverSimpleResponse> matchedCaregivers,
        List<MatchingCaregiverSimpleResponse> appliedCaregivers) {

    public static MatchingStatusDetailResponse of(
            Recruitment recruitment,
            List<MatchingCaregiverSimpleResponse> matchedCaregivers,
            List<MatchingCaregiverSimpleResponse> appliedCaregivers) {
        return new MatchingStatusDetailResponse(
                RecruitmentSimpleDto.from(recruitment), matchedCaregivers, appliedCaregivers);
    }
}
