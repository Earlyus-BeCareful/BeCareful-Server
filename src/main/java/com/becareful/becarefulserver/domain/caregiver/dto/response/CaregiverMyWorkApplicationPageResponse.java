package com.becareful.becarefulserver.domain.caregiver.dto.response;

import com.becareful.becarefulserver.domain.caregiver.dto.*;

public record CaregiverMyWorkApplicationPageResponse(boolean hasNewChat, WorkApplicationDto workApplicationDto) {
    public static CaregiverMyWorkApplicationPageResponse of(boolean hasNewChat, WorkApplicationDto workApplicationDto) {
        return new CaregiverMyWorkApplicationPageResponse(hasNewChat, workApplicationDto);
    }
}
