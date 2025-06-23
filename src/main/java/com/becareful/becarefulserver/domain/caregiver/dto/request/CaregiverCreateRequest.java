package com.becareful.becarefulserver.domain.caregiver.dto.request;

import com.becareful.becarefulserver.domain.caregiver.domain.vo.Certificate;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CaregiverCreateRequest(
        @NotBlank String realName,
        @NotBlank String birthYymmdd,
        @NotNull int genderCode,
        @NotBlank String phoneNumber,
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
        String profileImageUrl) {}
