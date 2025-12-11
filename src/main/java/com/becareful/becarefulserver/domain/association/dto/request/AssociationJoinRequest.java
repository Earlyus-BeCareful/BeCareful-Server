package com.becareful.becarefulserver.domain.association.dto.request;

import com.becareful.becarefulserver.domain.association.domain.AssociationRank;
import jakarta.validation.constraints.NotNull;

public record AssociationJoinRequest(
        @NotNull Long associationId,
        @NotNull AssociationRank associationRank,
        @NotNull Boolean isAgreedToTerms,
        @NotNull Boolean isAgreedToCollectPersonalInfo,
        @NotNull Boolean isAgreedToReceiveMarketingInfo) {}
