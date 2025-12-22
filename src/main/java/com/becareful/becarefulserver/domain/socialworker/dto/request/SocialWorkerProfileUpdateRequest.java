package com.becareful.becarefulserver.domain.socialworker.dto.request;

import com.becareful.becarefulserver.domain.nursing_institution.domain.vo.InstitutionRank;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record SocialWorkerProfileUpdateRequest(
        @NotBlank String realName,
        @NotBlank String nickName,
        @NotBlank String birthYymmdd,
        @NotNull int genderCode,
        @NotBlank String phoneNumber,
        @NotNull Long nursingInstitutionId,
        @NotNull InstitutionRank institutionRank,
        String profileImageTempKey) {}
