package com.becareful.becarefulserver.domain.matching.dto;

import com.becareful.becarefulserver.domain.caregiver.domain.Career;
import com.becareful.becarefulserver.domain.caregiver.domain.Caregiver;

public record MatchedCaregiverDto(CaregiverSimpleDto caregiverInfo, String applicationTitle) {
    public static MatchedCaregiverDto of(Caregiver caregiver, Career career) {
        return new MatchedCaregiverDto(CaregiverSimpleDto.from(caregiver), career.getTitle());
    }
}
