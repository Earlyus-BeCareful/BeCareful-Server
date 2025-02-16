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
import com.becareful.becarefulserver.domain.caregiver.domain.vo.CaregiverInfo;
import com.becareful.becarefulserver.domain.common.domain.BaseEntity;
import com.becareful.becarefulserver.domain.common.vo.Gender;

import java.time.LocalDate;
import java.util.List;
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

    private LocalDate birthDate;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    private String phoneNumber;

    private String password;

    @Embedded
    private Address address;

    private String profileImageUrl;

    @Embedded
    private CaregiverInfo caregiverInfo;

    private boolean isAgreedToTerms;

    private boolean isAgreedToCollectPersonalInfo;

    private boolean isAgreedToReceiveMarketingInfo;

    @Builder(access = AccessLevel.PRIVATE)
    private Caregiver(String name, LocalDate birthDate, Gender gender, String phoneNumber, String password,
            String profileImageUrl, Address address, CaregiverInfo caregiverInfo,
            boolean isAgreedToTerms, boolean isAgreedToCollectPersonalInfo,
            boolean isAgreedToReceiveMarketingInfo) {
        this.name = name;
        this.birthDate = birthDate;
        this.gender = gender;
        this.phoneNumber = phoneNumber;
        this.password = password;
        this.profileImageUrl = profileImageUrl;
        this.address = address;
        this.caregiverInfo = caregiverInfo;
        this.isAgreedToTerms = isAgreedToTerms;
        this.isAgreedToCollectPersonalInfo = isAgreedToCollectPersonalInfo;
        this.isAgreedToReceiveMarketingInfo = isAgreedToReceiveMarketingInfo;
    }

    public static Caregiver create(String name, LocalDate birthDate, String phoneNumber, String encodedPassword,
            Gender gender, String streetAddress, String detailAddress, CaregiverInfo caregiverInfo,
            boolean isAgreedToReceiveMarketingInfo, String profileImageUrl) {
        return Caregiver.builder()
                .name(name)
                .birthDate(birthDate)
                .gender(gender)
                .phoneNumber(phoneNumber)
                .password(encodedPassword)
                .profileImageUrl(profileImageUrl)
                .address(new Address(streetAddress, detailAddress))
                .caregiverInfo(caregiverInfo)
                .isAgreedToReceiveMarketingInfo(isAgreedToReceiveMarketingInfo)
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

    public List<String> getCertificateNames() {
        return this.getCaregiverInfo().getCertificateNames();
    }
}
