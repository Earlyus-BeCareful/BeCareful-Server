package com.becareful.becarefulserver.domain.socialworker.dto.request;

import jakarta.validation.constraints.NotNull;

public record SocialWorkerMyMarketingInfoReceivingAgreementUpdateRequest(
        @NotNull Boolean isAgreedToReceiveMarketingInfo) {}
