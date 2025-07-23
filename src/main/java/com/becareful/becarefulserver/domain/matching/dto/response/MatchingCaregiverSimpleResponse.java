package com.becareful.becarefulserver.domain.matching.dto.response;

import com.becareful.becarefulserver.domain.matching.domain.vo.MatchingResultStatus;
import com.becareful.becarefulserver.domain.matching.dto.CaregiverSimpleDto;

public record MatchingCaregiverSimpleResponse(
        CaregiverSimpleDto caregiverInfo, MatchingResultStatus matchingResultStatus) {
    public static MatchingCaregiverSimpleResponse of(
            CaregiverSimpleDto caregiverInfo, MatchingResultStatus matchingResultStatus) {
        return new MatchingCaregiverSimpleResponse(caregiverInfo, matchingResultStatus);
    }
}
