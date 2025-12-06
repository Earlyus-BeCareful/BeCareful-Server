package com.becareful.becarefulserver.domain.caregiver.dto.request;

import jakarta.validation.constraints.NotNull;

public record CaregiverMyMarketingInfoReceivingAgreementUpdateRequest(
        @NotNull Boolean isAgreedToReceiveMarketingInfo) {}
