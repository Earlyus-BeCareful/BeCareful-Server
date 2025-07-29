package com.becareful.becarefulserver.domain.nursing_institution.domain;

import static com.becareful.becarefulserver.global.constant.StaticResourceConstant.*;

import com.becareful.becarefulserver.domain.common.domain.*;
import com.becareful.becarefulserver.domain.nursing_institution.domain.converter.FacilityTypeConverter;
import com.becareful.becarefulserver.domain.nursing_institution.domain.vo.FacilityType;
import com.becareful.becarefulserver.domain.nursing_institution.dto.request.*;
import com.becareful.becarefulserver.domain.socialworker.domain.converter.*;
import com.becareful.becarefulserver.domain.socialworker.domain.vo.*;
import jakarta.persistence.*;
import java.util.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class NursingInstitution extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "nursing_institution_id")
    private Long id;

    private String name;

    private String code; // 기관 코드

    @Embedded
    private Address address;

    private int openYear;

    private String institutionPhoneNumber;

    private boolean isHavingBathCar;

    private String profileImageUrl;

    @Convert(converter = DetailCareTypeConverter.class)
    private EnumSet<DetailCareType> detailCareTypes;

    @Convert(converter = FacilityTypeConverter.class)
    private EnumSet<FacilityType> facilityTypes;

    @Builder(access = AccessLevel.PRIVATE)
    private NursingInstitution(
            String name,
            String code,
            int openYear,
            EnumSet<FacilityType> facilityTypes,
            String institutionPhoneNumber,
            Address address,
            String profileImageUrl) {
        this.name = name;
        this.code = code;
        this.openYear = openYear;
        this.facilityTypes = facilityTypes;
        this.institutionPhoneNumber = institutionPhoneNumber;
        this.address = address;
        this.profileImageUrl = profileImageUrl;
    }

    public static NursingInstitution create(
            String name,
            String code,
            int openYear,
            EnumSet<FacilityType> facilityTypeList,
            String institutionPhoneNumber,
            String streetAddress,
            String detailAddress,
            String profileImageUrl) {
        return NursingInstitution.builder()
                .name(name)
                .code(code)
                .openYear(openYear)
                .facilityTypes(
                        facilityTypeList == null || facilityTypeList.isEmpty()
                                ? EnumSet.noneOf(FacilityType.class)
                                : EnumSet.copyOf(facilityTypeList))
                .institutionPhoneNumber(institutionPhoneNumber)
                .address(new Address(streetAddress, detailAddress))
                .profileImageUrl(
                        profileImageUrl == null || profileImageUrl.isBlank()
                                ? INSTITUTION_DEFAULT_PROFILE_IMAGE_URL
                                : profileImageUrl)
                .build();
    }

    public void updateNursingInstitutionInfo(UpdateNursingInstitutionInfoRequest request) {
        EnumSet<FacilityType> facilityTypes =
                request.facilityTypeList() == null || request.facilityTypeList().isEmpty()
                        ? EnumSet.noneOf(FacilityType.class)
                        : EnumSet.copyOf(request.facilityTypeList());

        this.name = request.institutionName();
        this.code = request.institutionCode();
        this.openYear = request.openYear();
        this.facilityTypes = facilityTypes;
        this.institutionPhoneNumber = request.phoneNumber();
        this.profileImageUrl = request.profileImageUrl();
    }

    public void updateProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }
}
