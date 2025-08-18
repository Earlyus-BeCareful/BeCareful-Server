package com.becareful.becarefulserver.domain.caregiver.domain;

import static com.becareful.becarefulserver.global.constant.StaticResourceConstant.CAREGIVER_DEFAULT_PROFILE_IMAGE_URL;

import com.becareful.becarefulserver.domain.caregiver.domain.vo.Address;
import com.becareful.becarefulserver.domain.caregiver.domain.vo.CaregiverInfo;
import com.becareful.becarefulserver.domain.common.domain.BaseEntity;
import com.becareful.becarefulserver.domain.common.domain.Gender;
import jakarta.persistence.*;
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

    private String phoneNumber;

    private String profileImageUrl;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Embedded
    private Address address;

    @Embedded
    private CaregiverInfo caregiverInfo;

    private boolean isAgreedToTerms;

    private boolean isAgreedToCollectPersonalInfo;

    private boolean isAgreedToReceiveMarketingInfo;

    @Builder(access = AccessLevel.PRIVATE)
    private Caregiver(
            String name,
            LocalDate birthDate,
            Gender gender,
            String phoneNumber,
            String profileImageUrl,
            Address address,
            CaregiverInfo caregiverInfo,
            boolean isAgreedToTerms,
            boolean isAgreedToCollectPersonalInfo,
            boolean isAgreedToReceiveMarketingInfo) {
        this.name = name;
        this.birthDate = birthDate;
        this.gender = gender;
        this.phoneNumber = phoneNumber;
        this.profileImageUrl = profileImageUrl;
        this.address = address;
        this.caregiverInfo = caregiverInfo;
        this.isAgreedToTerms = isAgreedToTerms;
        this.isAgreedToCollectPersonalInfo = isAgreedToCollectPersonalInfo;
        this.isAgreedToReceiveMarketingInfo = isAgreedToReceiveMarketingInfo;
    }

    public static Caregiver create(
            String name,
            LocalDate birthDate,
            Gender gender,
            String phoneNumber,
            String profileImageUrl,
            String streetAddress,
            String detailAddress,
            CaregiverInfo caregiverInfo,
            boolean isAgreedToReceiveMarketingInfo) {
        return Caregiver.builder()
                .name(name)
                .birthDate(birthDate)
                .gender(gender)
                .phoneNumber(phoneNumber)
                .profileImageUrl(
                        profileImageUrl == null || profileImageUrl.isBlank()
                                ? CAREGIVER_DEFAULT_PROFILE_IMAGE_URL
                                : profileImageUrl)
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

    public int getAge() {
        return LocalDate.now().getYear() - this.birthDate.getYear() + 1;
    }

    public void updateInfo(String phoneNumber, CaregiverInfo caregiverInfo) {
        this.phoneNumber = phoneNumber;
        this.caregiverInfo = caregiverInfo;
    }
}
