package com.becareful.becarefulserver.domain.matching.dto.response;

import com.becareful.becarefulserver.domain.matching.domain.Recruitment;
import com.becareful.becarefulserver.domain.matching.dto.SocialWorkerRecruitmentSimpleDto;
import java.util.List;

public record MatchingStatusDetailResponse(
        SocialWorkerRecruitmentSimpleDto recruitmentInfo,
        List<MatchingCaregiverSimpleResponse> matchedCaregivers,
        List<MatchingCaregiverSimpleResponse> appliedCaregivers) {

    public static MatchingStatusDetailResponse of(
            Recruitment recruitment,
            List<MatchingCaregiverSimpleResponse> matchedCaregivers,
            List<MatchingCaregiverSimpleResponse> appliedCaregivers) {
        return new MatchingStatusDetailResponse(
                SocialWorkerRecruitmentSimpleDto.from(recruitment), matchedCaregivers, appliedCaregivers);
    }
}
