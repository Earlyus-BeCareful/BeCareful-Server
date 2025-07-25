package com.becareful.becarefulserver.domain.matching.dto.response;

import com.becareful.becarefulserver.domain.matching.domain.vo.MatchingResultStatus;
import com.becareful.becarefulserver.domain.matching.dto.MatchedCaregiverDto;

public record MatchingCaregiverSimpleResponse(
        MatchedCaregiverDto caregiverInfo, MatchingResultStatus matchingResultStatus) {
    public static MatchingCaregiverSimpleResponse of(
            MatchedCaregiverDto caregiverInfo, MatchingResultStatus matchingResultStatus) {
        return new MatchingCaregiverSimpleResponse(caregiverInfo, matchingResultStatus);
    }
}
