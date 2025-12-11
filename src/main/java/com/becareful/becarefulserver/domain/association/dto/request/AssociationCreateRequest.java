package com.becareful.becarefulserver.domain.association.dto.request;

import jakarta.validation.constraints.*;
import jakarta.validation.constraints.NotNull;

public record AssociationCreateRequest(
        @NotBlank String name,
        @NotBlank String profileImageTempKey,
        Integer establishedYear,
        @NotNull Boolean isAgreedToTerms,
        @NotNull Boolean isAgreedToCollectPersonalInfo,
        @NotNull Boolean isAgreedToReceiveMarketingInfo) {}
