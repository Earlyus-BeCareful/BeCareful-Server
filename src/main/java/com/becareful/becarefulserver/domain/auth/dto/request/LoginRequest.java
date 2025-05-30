package com.becareful.becarefulserver.domain.auth.dto.request;

import jakarta.validation.constraints.NotBlank;

public record LoginRequest(@NotBlank String phoneNumber, @NotBlank String password) {}
