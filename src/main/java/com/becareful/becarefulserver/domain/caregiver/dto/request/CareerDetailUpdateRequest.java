package com.becareful.becarefulserver.domain.caregiver.dto.request;

import jakarta.validation.constraints.NotBlank;

public record CareerDetailUpdateRequest(@NotBlank String workInstitution, @NotBlank String workYear) {}
