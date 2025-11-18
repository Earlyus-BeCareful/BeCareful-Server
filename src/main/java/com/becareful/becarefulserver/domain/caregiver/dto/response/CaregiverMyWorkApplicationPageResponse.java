package com.becareful.becarefulserver.domain.caregiver.dto.response;

import com.becareful.becarefulserver.domain.caregiver.dto.*;

public record CaregiverMyWorkApplicationPageResponse(
        boolean hasNewChat, boolean hasCareer, WorkApplicationDto workApplicationDto) {
    public static CaregiverMyWorkApplicationPageResponse of(
            boolean hasNewChat, boolean hasCareer, WorkApplicationDto workApplicationDto) {
        return new CaregiverMyWorkApplicationPageResponse(hasNewChat, hasCareer, workApplicationDto);
    }
}
