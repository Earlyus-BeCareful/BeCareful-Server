package com.becareful.becarefulserver.domain.caregiver.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import com.becareful.becarefulserver.domain.caregiver.domain.CareerType;

import java.util.List;

public record CareerUpdateRequest(
        @NotBlank String title,
        @NotNull CareerType careerType,
        String introduce,
        List<CareerDetailUpdateRequest> careerDetails
) {}
