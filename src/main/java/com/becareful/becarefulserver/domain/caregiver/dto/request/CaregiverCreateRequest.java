package com.becareful.becarefulserver.domain.caregiver.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import com.becareful.becarefulserver.domain.caregiver.domain.vo.Certificate;
import com.becareful.becarefulserver.domain.common.vo.Gender;

import java.time.LocalDate;

public record CaregiverCreateRequest(
    @NotBlank String name,
    @NotNull LocalDate birthDate,
    @NotNull Gender gender,
    @NotNull String phoneNumber,
    @NotNull String password,
    @NotNull String streetAddress,
    @NotNull String detailAddress,
    @NotNull Certificate caregiverCertificate,
    Certificate socialWorkerCertificate,
    Certificate nursingCareCertificate,
    @NotNull boolean isHavingCar,
    @NotNull boolean isCompleteDementiaEducation,
    @NotNull boolean isAgreedToTerms,
    @NotNull boolean isAgreedToCollectPersonalInfo,
    @NotNull boolean isAgreedToReceiveMarketingInfo,
    String profileImageUrl
) {}
