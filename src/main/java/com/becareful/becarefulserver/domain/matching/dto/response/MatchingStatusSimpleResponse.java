package com.becareful.becarefulserver.domain.matching.dto.response;

import com.becareful.becarefulserver.domain.matching.domain.Recruitment;
import com.becareful.becarefulserver.domain.matching.dto.ElderlySimpleDto;

public record MatchingStatusSimpleResponse(
        Long recruitmentId, ElderlySimpleDto elderlyInfo, int matchingCount, int applyCount) {
    public static MatchingStatusSimpleResponse of(Recruitment recruitment, int matchingCount, int applyCount) {
        return new MatchingStatusSimpleResponse(
                recruitment.getId(),
                ElderlySimpleDto.from(recruitment.getElderly()),
                matchingCount + applyCount,
                applyCount);
    }
}
