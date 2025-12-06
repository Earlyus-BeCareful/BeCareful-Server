package com.becareful.becarefulserver.domain.caregiver.dto.response;

import com.becareful.becarefulserver.domain.caregiver.domain.Caregiver;

public record CaregiverMySettingResponse(boolean isAgreedToReceiveMarketingInfo) {

    public static CaregiverMySettingResponse from(Caregiver caregiver) {
        return new CaregiverMySettingResponse(caregiver.isAgreedToReceiveMarketingInfo());
    }
}
