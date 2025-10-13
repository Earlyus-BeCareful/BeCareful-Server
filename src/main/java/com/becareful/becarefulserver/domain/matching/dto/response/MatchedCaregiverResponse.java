package com.becareful.becarefulserver.domain.matching.dto.response;

import com.becareful.becarefulserver.domain.caregiver.domain.Career;
import com.becareful.becarefulserver.domain.caregiver.domain.Caregiver;
import com.becareful.becarefulserver.domain.matching.dto.CaregiverSimpleDto;

public record MatchedCaregiverResponse(CaregiverSimpleDto caregiverInfo, String applicationTitle) {
    public static MatchedCaregiverResponse of(Caregiver caregiver, Career career) {
        return new MatchedCaregiverResponse(CaregiverSimpleDto.from(caregiver), career.getTitle());
    }
}
