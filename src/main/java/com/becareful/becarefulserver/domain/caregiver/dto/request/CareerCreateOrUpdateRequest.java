package com.becareful.becarefulserver.domain.caregiver.dto.request;

import com.becareful.becarefulserver.domain.caregiver.domain.CareerType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public record CareerCreateOrUpdateRequest(
        @NotBlank String title,
        @NotNull CareerType careerType,
        String introduce,
        List<CareerDetailUpdateRequest> careerDetails) {}
