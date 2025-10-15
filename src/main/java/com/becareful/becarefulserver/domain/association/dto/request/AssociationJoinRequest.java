package com.becareful.becarefulserver.domain.association.dto.request;

import com.becareful.becarefulserver.domain.association.domain.vo.AssociationRank;
import jakarta.validation.constraints.NotNull;

public record AssociationJoinRequest(
        @NotNull Long associationId,
        @NotNull AssociationRank associationRank,
        boolean isAgreedToTerms,
        boolean isAgreedToCollectPersonalInfo,
        boolean isAgreedToReceiveMarketingInfo) {}
