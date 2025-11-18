package com.becareful.becarefulserver.domain.socialworker.dto.request;

import jakarta.validation.constraints.NotNull;

public record SocialWorkerMyAssociationDetailUpdateRequest(@NotNull Boolean isAgreedToReceiveMarketingInfo) {}
