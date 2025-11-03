package com.becareful.becarefulserver.domain.association.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AssociationCreateRequest(
        @NotBlank String name,
        String profileImageUrl,
        Integer establishedYear,
        @NotNull Boolean isAgreedToTerms,
        @NotNull Boolean isAgreedToCollectPersonalInfo,
        @NotNull Boolean isAgreedToReceiveMarketingInfo) {}
