package com.becareful.becarefulserver.domain.caregiver.dto.request;

import com.becareful.becarefulserver.domain.caregiver.domain.vo.Address;
import com.becareful.becarefulserver.global.common.vo.Gender;
import com.becareful.becarefulserver.domain.caregiver.domain.Caregiver;

public record CaregiverCreateRequest(
    String name,
    Gender gender,
    String phoneNumber,
    String password,
    String streetAddress,
    String detailAddress,
    boolean isHavingCar,
    boolean isCompleteDementiaEducation,
    boolean isAgreedToTerms,
    boolean isAgreedToCollectPersonalInfo,
    boolean isAgreedToReceiveMarketingInfo,
    String profileImageUrl
) {}
