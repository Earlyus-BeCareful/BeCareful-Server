package com.becareful.becarefulserver.domain.socialworker.dto.request;

import com.becareful.becarefulserver.domain.nursing_institution.vo.InstitutionRank;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record SocialWorkerUpdateBasicInfoRequest(
        @NotBlank String realName,
        @NotBlank String nickName,
        @NotBlank String birthYymmdd,
        @NotNull int genderCode,
        @NotBlank String phoneNumber,
        @NotNull Long nursingInstitutionId,
        @NotNull InstitutionRank institutionRank,
        @NotNull boolean isAgreedToTerms,
        @NotNull boolean isAgreedToCollectPersonalInfo,
        @NotNull boolean isAgreedToReceiveMarketingInfo) {}
