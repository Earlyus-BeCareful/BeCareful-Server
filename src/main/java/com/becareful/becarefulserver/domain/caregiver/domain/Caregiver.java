package com.becareful.becarefulserver.domain.caregiver.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import com.becareful.becarefulserver.domain.caregiver.domain.vo.Address;
import com.becareful.becarefulserver.global.common.domain.BaseEntity;
import com.becareful.becarefulserver.global.common.vo.Gender;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Caregiver extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "caregiver_id")
    private Long id;

    private String name;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    private String phoneNumber;

    private String password;

    @Embedded
    private Address address;

    private boolean isHavingCar;

    private boolean isCompleteDementiaEducation;

    private boolean isAgreedToTerms;

    private boolean isAgreedToCollectPersonalInfo;

    private boolean isAgreedToReceiveMarketingInfo;

    private String profileImageUrl;

    @Builder(access = AccessLevel.PRIVATE)
    private Caregiver(String name, Gender gender, String phoneNumber, String password,
            Address address,
            boolean isHavingCar, boolean isCompleteDementiaEducation, boolean isAgreedToTerms,
            boolean isAgreedToCollectPersonalInfo, boolean isAgreedToReceiveMarketingInfo,
            String profileImageUrl) {
        this.name = name;
        this.gender = gender;
        this.phoneNumber = phoneNumber;
        this.password = password;
        this.address = address;
        this.isHavingCar = isHavingCar;
        this.isCompleteDementiaEducation = isCompleteDementiaEducation;
        this.isAgreedToTerms = isAgreedToTerms;
        this.isAgreedToCollectPersonalInfo = isAgreedToCollectPersonalInfo;
        this.isAgreedToReceiveMarketingInfo = isAgreedToReceiveMarketingInfo;
        this.profileImageUrl = profileImageUrl;
    }

    public static Caregiver create(String name, String phoneNumber, String encodedPassword,
            Gender gender, String streetAddress, String detailAddress, boolean isHavingCar,
            boolean isCompleteDementiaEducation, boolean isAgreedToReceiveMarketingInfo,
            String profileImageUrl) {
        return Caregiver.builder()
                .name(name)
                .gender(gender)
                .phoneNumber(phoneNumber)
                .password(encodedPassword)
                .address(new Address(streetAddress, detailAddress))
                .isHavingCar(isHavingCar)
                .isCompleteDementiaEducation(isCompleteDementiaEducation)
                .isAgreedToReceiveMarketingInfo(isAgreedToReceiveMarketingInfo)
                .profileImageUrl(profileImageUrl)
                .isAgreedToTerms(true)
                .isAgreedToCollectPersonalInfo(true)
                .build();
    }

    /***
     * Entity Method
     * */

    public void updateProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }
}
