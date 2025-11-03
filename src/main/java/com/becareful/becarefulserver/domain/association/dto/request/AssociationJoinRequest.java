package com.becareful.becarefulserver.domain.association.dto.request;

import com.becareful.becarefulserver.domain.socialworker.domain.vo.AssociationRank;
import jakarta.validation.constraints.NotNull;

public record AssociationJoinRequest(
        @NotNull Long associationId,
        @NotNull AssociationRank associationRank,
        @NotNull boolean isAgreedToTerms,
        @NotNull boolean isAgreedToCollectPersonalInfo,
        @NotNull boolean isAgreedToReceiveMarketingInfo) {}
