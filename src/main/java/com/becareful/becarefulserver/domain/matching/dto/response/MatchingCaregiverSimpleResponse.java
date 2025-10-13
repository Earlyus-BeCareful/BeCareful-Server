package com.becareful.becarefulserver.domain.matching.dto.response;

import com.becareful.becarefulserver.domain.matching.domain.vo.MatchingResultStatus;

public record MatchingCaregiverSimpleResponse(
        MatchedCaregiverResponse caregiverInfo, MatchingResultStatus matchingResultStatus) {
    public static MatchingCaregiverSimpleResponse of(
            MatchedCaregiverResponse caregiverInfo, MatchingResultStatus matchingResultStatus) {
        return new MatchingCaregiverSimpleResponse(caregiverInfo, matchingResultStatus);
    }
}
