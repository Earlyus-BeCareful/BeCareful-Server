package com.becareful.becarefulserver.domain.socialworker.domain;

import static com.becareful.becarefulserver.global.constant.StaticResourceConstant.CAREGIVER_DEFAULT_PROFILE_IMAGE_URL;
import static com.becareful.becarefulserver.global.constant.StaticResourceConstant.ELDERLY_DEFAULT_PROFILE_IMAGE_URL;

import com.becareful.becarefulserver.domain.common.domain.BaseEntity;
import com.becareful.becarefulserver.domain.common.domain.DetailCareType;
import com.becareful.becarefulserver.domain.common.vo.Gender;
import com.becareful.becarefulserver.domain.common.vo.Location;
import com.becareful.becarefulserver.domain.nursingInstitution.domain.NursingInstitution;
import com.becareful.becarefulserver.domain.socialworker.domain.converter.DetailCareTypeConverter;
import com.becareful.becarefulserver.domain.socialworker.domain.vo.CareLevel;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.EnumSet;
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
            String siDo,
            String siGuGun,
            String eupMyeonDong,
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
                .hasInmate(hasInmate)
                .residentialLocation(Location.of(siDo, siGuGun, eupMyeonDong))
                .detailAddress(detailAddress)
                .hasPet(hasPet)
                .profileImageUrl(
                        profileImageUrl == null || profileImageUrl.isBlank()
                                ? ELDERLY_DEFAULT_PROFILE_IMAGE_URL
                                : profileImageUrl)
                .institution(institution)
                .careLevel(careLevel)
                .healthCondition(healthCondition)
                .detailCareTypes(detailCareTypes)
                .build();
    }

    public void updateProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = (profileImageUrl == null || profileImageUrl.isBlank())
                ? CAREGIVER_DEFAULT_PROFILE_IMAGE_URL
                : profileImageUrl;
    }

    public Integer getAge() {
        return LocalDate.now().getYear() - birthday.getYear() + 1;
    }

    public void updateName(String name) {
        if (name != null) this.name = name;
    }

    public void updateBirthday(LocalDate birthday) {
        if (birthday != null) this.birthday = birthday;
    }

    public void updateInmate(Boolean inmate) {
        if (inmate != null) this.hasInmate = inmate;
    }

    public void updatePet(Boolean pet) {
        if (pet != null) this.hasPet = pet;
    }

    public void updateGender(Gender gender) {
        if (gender != null) this.gender = gender;
    }

    public void updateCareLevel(CareLevel careLevel) {
        if (careLevel != null) this.careLevel = careLevel;
    }

    public void updateResidentialLocation(String siDo, String siGuGun, String eupMyeonDong) {
        if (siDo != null || siGuGun != null || eupMyeonDong != null) {
            this.residentialLocation = Location.of(
                    siDo != null ? siDo : this.residentialLocation.getSiDo(),
                    siGuGun != null ? siGuGun : this.residentialLocation.getSiGuGun(),
                    eupMyeonDong != null ? eupMyeonDong : this.residentialLocation.getEupMyeonDong());
        }
    }

    public void updateDetailAddress(String detailAddress) {
        if (detailAddress != null) this.detailAddress = detailAddress;
    }

    public void updateHealthCondition(String healthCondition) {
        if (healthCondition != null) this.healthCondition = healthCondition;
    }

    public void updateDetailCareTypes(EnumSet<DetailCareType> detailCareTypes) {
        if (detailCareTypes != null) this.detailCareTypes = detailCareTypes;
    }
}
