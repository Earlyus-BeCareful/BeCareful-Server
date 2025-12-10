package com.becareful.becarefulserver.domain.caregiver.dto.response;

import com.becareful.becarefulserver.domain.caregiver.dto.*;

public record CaregiverMyWorkApplicationPageResponse(boolean hasCareer, WorkApplicationDto workApplicationDto) {
    public static CaregiverMyWorkApplicationPageResponse of(boolean hasCareer, WorkApplicationDto workApplicationDto) {
        return new CaregiverMyWorkApplicationPageResponse(hasCareer, workApplicationDto);
    }
}
