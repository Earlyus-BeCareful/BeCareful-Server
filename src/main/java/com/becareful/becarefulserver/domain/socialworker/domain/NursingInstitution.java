package com.becareful.becarefulserver.domain.socialworker.domain;

import static com.becareful.becarefulserver.global.constant.StaticResourceConstant.INSTITUTION_DEFAULT_PROFILE_IMAGE_URL;

import com.becareful.becarefulserver.domain.common.domain.BaseEntity;
import com.becareful.becarefulserver.domain.socialworker.domain.vo.Address;

import jakarta.persistence.*;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class NursingInstitution extends BaseEntity {

    @Id
    @Column(name = "nursing_institution_id")
    private String id;

    private String name;

    @Embedded
    private Address address;

    private LocalDateTime openDate;

    private String institutionPhoneNumber;

    private boolean isHavingBathCar;

    private String profileImageUrl;

    @Builder(access = AccessLevel.PRIVATE)
    private NursingInstitution(String id, String name, Address address, LocalDateTime openDate,
            String institutionPhoneNumber, boolean isHavingBathCar, String profileImageUrl) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.openDate = openDate;
        this.institutionPhoneNumber = institutionPhoneNumber;
        this.isHavingBathCar = isHavingBathCar;
        this.profileImageUrl = profileImageUrl;
    }

    public static NursingInstitution create(String id, String name, String streetAddress,
            String detailAddress, String institutionPhoneNumber, boolean isHavingBathCar,
            LocalDateTime openDate, String profileImageUrl) {
        return NursingInstitution.builder()
                .id(id)
                .name(name)
                .address(new Address(streetAddress, detailAddress))
                .institutionPhoneNumber(institutionPhoneNumber)
                .isHavingBathCar(isHavingBathCar)
                .openDate(openDate)
                .profileImageUrl(
                        profileImageUrl == null || profileImageUrl.isBlank()
                                ? INSTITUTION_DEFAULT_PROFILE_IMAGE_URL
                                : profileImageUrl
                )
                .build();
    }

    public void updateProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }
}
