package com.becareful.becarefulserver.domain.caregiver.dto.request;

import com.becareful.becarefulserver.common.vo.Gender;
import com.becareful.becarefulserver.domain.caregiver.domain.Caregiver;

public record CaregiverCreateRequest(
    String name,
    Gender gender,
    String phoneNumber,
    boolean isHavingCar,
    boolean isCompleteDementiaEducation,
    boolean isAgreedToTerms,
    boolean isAgreedToCollectPersonalInfo,
    boolean isAgreedToReceiveMarketingInfo,
    String profileImageUrl
) {

    public Caregiver toEntity() {
        return Caregiver.builder()
                .name(name)
                .gender(gender)
                .phoneNumber(phoneNumber)
                .isHavingCar(isHavingCar)
                .isCompleteDementiaEducation(isCompleteDementiaEducation)
                .isAgreedToTerms(isAgreedToTerms)
                .isAgreedToCollectPersonalInfo(isAgreedToCollectPersonalInfo)
                .isAgreedToReceiveMarketingInfo(isAgreedToReceiveMarketingInfo)
                .profileImageUrl(profileImageUrl)
                .build();
    }
}
