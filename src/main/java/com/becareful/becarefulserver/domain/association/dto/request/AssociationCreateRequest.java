package com.becareful.becarefulserver.domain.association.dto.request;

import jakarta.validation.constraints.NotBlank;

public record AssociationCreateRequest(
        @NotBlank String name, @NotBlank String profileImageTempKey, Integer establishedYear) {}
