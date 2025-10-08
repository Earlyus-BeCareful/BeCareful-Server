package com.becareful.becarefulserver.domain.socialworker.domain;

import static com.becareful.becarefulserver.global.constant.StaticResourceConstant.ELDERLY_DEFAULT_PROFILE_IMAGE_URL;

import com.becareful.becarefulserver.domain.common.domain.BaseEntity;
import com.becareful.becarefulserver.domain.common.domain.CareType;
import com.becareful.becarefulserver.domain.common.domain.DetailCareType;
import com.becareful.becarefulserver.domain.common.domain.Gender;
import com.becareful.becarefulserver.domain.common.domain.vo.Location;
import com.becareful.becarefulserver.domain.nursing_institution.domain.NursingInstitution;
import com.becareful.becarefulserver.domain.socialworker.domain.converter.DetailCareTypeConverter;
import com.becareful.becarefulserver.domain.socialworker.domain.vo.CareLevel;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Elderly extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "elderly_id")
    private Long id;

    private String name;

    private LocalDate birthday;

    private boolean hasInmate;

    private boolean hasPet;

    private String detailAddress;

    private String profileImageUrl;

    private String healthCondition;

    @Enumerated(EnumType.STRING)
    Gender gender;

    @Enumerated(EnumType.STRING)
    CareLevel careLevel;

    @Embedded
    private Location residentialLocation;

    @Convert(converter = DetailCareTypeConverter.class)
    private EnumSet<DetailCareType> detailCareTypes;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "nursing_institution_id")
    private NursingInstitution nursingInstitution;

    @Builder(access = AccessLevel.PRIVATE)
    private Elderly(
            Long id,
            String name,
            LocalDate birthday,
            Gender gender,
            Location residentialLocation,
            String detailAddress,
            Boolean hasInmate,
            Boolean hasPet,
            NursingInstitution institution,
            String profileImageUrl,
            CareLevel careLevel,
            String healthCondition,
            EnumSet<DetailCareType> detailCareTypes) {
        this.id = id;
        this.name = name;
        this.birthday = birthday;
        this.gender = gender;
        this.residentialLocation = residentialLocation;
        this.detailAddress = detailAddress;
        this.hasInmate = hasInmate;
        this.hasPet = hasPet;
        this.nursingInstitution = institution;
        this.profileImageUrl = profileImageUrl;
        this.careLevel = careLevel;
        this.healthCondition = healthCondition;
        this.detailCareTypes = detailCareTypes;
    }

    public static Elderly create(
            String name,
            LocalDate birthday,
            Gender gender,
            Location residentialLocation,
            String detailAddress,
            boolean hasInmate,
            boolean hasPet,
            String profileImageUrl,
            NursingInstitution institution,
            CareLevel careLevel,
            String healthCondition,
            EnumSet<DetailCareType> detailCareTypes) {
        return Elderly.builder()
                .name(name)
                .birthday(birthday)
                .gender(gender)
                .residentialLocation(residentialLocation)
                .detailAddress(detailAddress)
                .profileImageUrl(
                        profileImageUrl == null || profileImageUrl.isBlank()
                                ? ELDERLY_DEFAULT_PROFILE_IMAGE_URL
                                : profileImageUrl)
                .institution(institution)
                .careLevel(careLevel)
                .healthCondition(healthCondition)
                .detailCareTypes(detailCareTypes)
                .hasInmate(hasInmate)
                .hasPet(hasPet)
                .build();
    }

    public void updateProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = (profileImageUrl == null || profileImageUrl.isBlank())
                ? ELDERLY_DEFAULT_PROFILE_IMAGE_URL
                : profileImageUrl;
    }

    public Integer getAge() {
        return LocalDate.now().getYear() - birthday.getYear() + 1;
    }

    public Map<CareType, List<String>> getDetailCareTypeMap() {
        return detailCareTypes.stream()
                .collect(Collectors.groupingBy(
                        DetailCareType::getCareType,
                        Collectors.mapping(DetailCareType::getDisplayName, Collectors.toList())));
    }

    /**
     * Entity Method
     */

    // TODO : 업데이트 로직을 PUT 방식으로 수정하지 않을 데이터는 기존값을 보내도록 API 명세 수정
    public void update(
            String name,
            LocalDate birthday,
            Gender gender,
            Boolean hasInmate,
            Boolean hasPet,
            CareLevel careLevel,
            String siDo,
            String siGuGun,
            String eupMyeonDong,
            String detailAddress,
            String healthCondition,
            String profileImageUrl,
            List<DetailCareType> detailCareTypes) {

        if (name != null) {
            this.name = name;
        }

        if (gender != null) {
            this.gender = gender;
        }

        if (birthday != null) {
            this.birthday = birthday;
        }

        if (hasInmate != null) {
            this.hasInmate = hasInmate;
        }

        if (hasPet != null) {
            this.hasPet = hasPet;
        }

        if (careLevel != null) {
            this.careLevel = careLevel;
        }

        if (siDo != null && siGuGun != null && eupMyeonDong != null) {
            this.residentialLocation = Location.of(siDo, siGuGun, eupMyeonDong);
        }

        if (detailAddress != null) {
            this.detailAddress = detailAddress;
        }

        if (healthCondition != null) {
            this.healthCondition = healthCondition;
        }

        updateProfileImageUrl(profileImageUrl);

        if (detailCareTypes != null) {
            this.detailCareTypes = EnumSet.copyOf(detailCareTypes);
        }
    }
}
