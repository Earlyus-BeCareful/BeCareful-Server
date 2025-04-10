package com.becareful.becarefulserver.domain.socialworker.dto.request;


import com.becareful.becarefulserver.domain.socialworker.domain.vo.AssociationRank;
import com.becareful.becarefulserver.domain.nursingInstitution.vo.InstitutionRank;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record SocialworkerCreateRequest(
        Long nursingInstitutionId,
        @NotBlank String realName,
        @NotBlank String nickName,
        @NotBlank String birthYymmdd,
        @NotNull int genderCode,
        @NotBlank String phoneNumber,
        @NotNull InstitutionRank institutionRank,
        @NotNull AssociationRank associationRank,
        boolean isAgreedToTerms,
        boolean isAgreedToCollectPersonalInfo,
        boolean isAgreedToReceiveMarketingInfo
       ) {}

