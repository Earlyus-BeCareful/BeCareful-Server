package com.becareful.becarefulserver.domain.caregiver.dto.request;

import com.becareful.becarefulserver.domain.caregiver.domain.vo.Address;
import com.becareful.becarefulserver.global.common.vo.Gender;
import com.becareful.becarefulserver.domain.caregiver.domain.Caregiver;

public record CaregiverCreateRequest(
    String name,
    Gender gender,
    String phoneNumber,
    String streetAddress,
    String detailAddress,
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
                .address(new Address(streetAddress, detailAddress))
                .isHavingCar(isHavingCar)
                .isCompleteDementiaEducation(isCompleteDementiaEducation)
                .isAgreedToTerms(isAgreedToTerms)
                .isAgreedToCollectPersonalInfo(isAgreedToCollectPersonalInfo)
                .isAgreedToReceiveMarketingInfo(isAgreedToReceiveMarketingInfo)
                .profileImageUrl(profileImageUrl)
                .build();
    }
}
