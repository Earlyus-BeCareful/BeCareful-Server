package com.becareful.becarefulserver.domain.association.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AssociationCreateRequest(
        @NotBlank String name,
        String profileImageUrl,
        Integer establishedYear,
        @NotNull boolean isAgreedToTerms,
        @NotNull boolean isAgreedToCollectPersonalInfo,
        @NotNull boolean isAgreedToReceiveMarketingInfo) {}
