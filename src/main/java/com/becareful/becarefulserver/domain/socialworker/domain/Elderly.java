package com.becareful.becarefulserver.domain.socialworker.domain;

import com.becareful.becarefulserver.domain.common.domain.BaseEntity;
import com.becareful.becarefulserver.domain.common.domain.DetailCareType;
import com.becareful.becarefulserver.domain.common.vo.Gender;
import com.becareful.becarefulserver.domain.socialworker.domain.converter.DetailCareTypeConverter;
import com.becareful.becarefulserver.domain.socialworker.domain.vo.CareLevel;
import com.becareful.becarefulserver.domain.socialworker.domain.vo.ResidentialAddress;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import lombok.NoArgsConstructor;

import static com.becareful.becarefulserver.global.constant.StaticResourceConstant.CAREGIVER_DEFAULT_PROFILE_IMAGE_URL;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Elderly extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    String name;

    LocalDate birthday;

    boolean inmate;

    boolean pet;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "nursing_institution_id")
    private NursingInstitution nursingInstitution;

    @Enumerated(EnumType.STRING)
    Gender gender;

    @Enumerated(EnumType.STRING)
    CareLevel careLevel;

    @Embedded
    private ResidentialAddress address;

    private String profileImageUrl;

    private String healthCondition;

    @Convert(converter = DetailCareTypeConverter.class)
    private EnumSet<DetailCareType> detailCareTypes;

    @Builder(access = AccessLevel.PRIVATE)
    private Elderly(Long id, String name,LocalDate birthday,  Gender gender,
                    Boolean inmate, Boolean pet, NursingInstitution institution, String profileImageUrl,
                    CareLevel careLevel, String healthCondition, EnumSet<DetailCareType> detailCareTypes){
        this.id = id;
        this.name = name;
        this.birthday = birthday;
        this.gender = gender;
        this.inmate = inmate;
        this.pet = pet;
        this.nursingInstitution = institution;
        this.profileImageUrl = profileImageUrl;
        this.careLevel = careLevel;
        this.healthCondition = healthCondition;
        this.detailCareTypes =detailCareTypes;
    }

    public static Elderly create(String name,LocalDate birthday,  Gender gender,
                                 boolean inmate, boolean pet,  String profileImageUrl, NursingInstitution institution,
                                 CareLevel careLevel, String healthCondition, EnumSet<DetailCareType> detailCareTypes){
        return Elderly.builder()
                .name(name)
                .birthday(birthday)
                .gender(gender)
                .inmate(inmate)
                .pet(pet)
                .profileImageUrl(profileImageUrl == null || profileImageUrl.isBlank()
                        ? CAREGIVER_DEFAULT_PROFILE_IMAGE_URL
                        : profileImageUrl)
                .institution(institution)
                .careLevel(careLevel)
                .healthCondition(healthCondition)
                .detailCareTypes(detailCareTypes)
                .build();
    }

    public void updateProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }

    public Integer getAge() {
        return LocalDate.now().getYear() - birthday.getYear() + 1;
    }
}
