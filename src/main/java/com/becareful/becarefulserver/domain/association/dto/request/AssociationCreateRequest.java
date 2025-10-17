package com.becareful.becarefulserver.domain.association.dto.request;

import jakarta.validation.constraints.NotBlank;

public record AssociationCreateRequest(
        @NotBlank String name,
        String profileImageUrl,
        Integer establishedYear,
        boolean isAgreedToTerms,
        boolean isAgreedToCollectPersonalInfo,
        boolean isAgreedToReceiveMarketingInfo) {}
