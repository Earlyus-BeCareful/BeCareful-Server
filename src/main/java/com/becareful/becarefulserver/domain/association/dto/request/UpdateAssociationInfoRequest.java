package com.becareful.becarefulserver.domain.association.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UpdateAssociationInfoRequest(
        @NotBlank String associationImageUrl,
        @NotBlank String associationName,
        @NotNull Integer associationEstablishedYear) {}
