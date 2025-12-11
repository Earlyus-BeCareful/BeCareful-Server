package com.becareful.becarefulserver.domain.caregiver.dto.response;

import com.becareful.becarefulserver.domain.caregiver.dto.*;

public record CaregiverMyWorkApplicationPageResponse(
        String caregiverName, boolean hasCareer, WorkApplicationDto workApplicationDto) {
    public static CaregiverMyWorkApplicationPageResponse of(
            String caregiverName, boolean hasCareer, WorkApplicationDto workApplicationDto) {
        return new CaregiverMyWorkApplicationPageResponse(caregiverName, hasCareer, workApplicationDto);
    }
}
