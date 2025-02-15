package com.becareful.becarefulserver.domain.socialworker.dto.request;

import jakarta.validation.constraints.NotBlank;


import java.time.LocalDateTime;

public record NursingInstitutionCreateRequest (
    @NotBlank String institutionId,
    @NotBlank String institutionName,
    @NotBlank String streetAddress,
    String detailAddress,
    String phoneNumber,
    @NotBlank LocalDateTime opendDate,
    @NotBlank boolean isHavingBathCar,
    String profileImageUrl
){}
