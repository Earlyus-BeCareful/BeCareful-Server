package com.becareful.becarefulserver.domain.socialworker.dto.request;


import com.becareful.becarefulserver.domain.common.vo.Gender;
import com.becareful.becarefulserver.domain.socialworker.domain.vo.Rank;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record SocialworkerCreateRequest(
    @NotBlank String name,
    Gender gender,
    @NotBlank String phoneNumber,
    @NotNull String password,
    @NotNull String institutionId,
    @NotNull Rank rank,
    boolean isAgreedToTerms,
    boolean isAgreedToCollectPersonalInfo,
    boolean isAgreedToReceiveMarketingInfo,
    String socialProfileImageUrl
) {}

