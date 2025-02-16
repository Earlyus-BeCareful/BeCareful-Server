package com.becareful.becarefulserver.domain.caregiver.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CareerDetailUpdateRequest(
        @NotBlank String workInstitution,
        @NotNull Integer workYear) {}
