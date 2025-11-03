package com.becareful.becarefulserver.domain.association.domain;

import com.becareful.becarefulserver.domain.common.domain.BaseEntity;
import com.becareful.becarefulserver.domain.common.domain.Gender;
import com.becareful.becarefulserver.domain.nursing_institution.domain.NursingInstitution;
import com.becareful.becarefulserver.domain.nursing_institution.domain.vo.InstitutionRank;
import com.becareful.becarefulserver.domain.socialworker.domain.vo.AssociationRank;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.Period;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AssociationMember extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "social_worker_id")
    private Long id;

    private String name;

    private String nickname;

    private LocalDate birthday;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    private String phoneNumber;

    @Enumerated(EnumType.STRING)
    private InstitutionRank institutionRank;

    @Enumerated(EnumType.STRING)
    private AssociationRank associationRank;

    private boolean isAgreedToTerms;

    private boolean isAgreedToCollectPersonalInfo;

    private boolean isAgreedToReceiveMarketingInfo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "nursing_institution_id")
    private NursingInstitution institution;

    @Setter(AccessLevel.PUBLIC)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "association_id")
    private Association association;

    @Builder(access = AccessLevel.PRIVATE)
    private AssociationMember(
            String name,
            String nickname,
            LocalDate birthday,
            Gender gender,
            String phoneNumber,
            NursingInstitution institution,
            InstitutionRank institutionRank,
            Association association,
            AssociationRank associationRank,
            boolean isAgreedToTerms,
            boolean isAgreedToCollectPersonalInfo,
            boolean isAgreedToReceiveMarketingInfo) {
        this.name = name;
        this.nickname = nickname;
        this.birthday = birthday;
        this.gender = gender;
        this.phoneNumber = phoneNumber;
        this.institutionRank = institutionRank;
        this.association = association;
        this.associationRank = associationRank;
        this.institution = institution;
        this.isAgreedToTerms = isAgreedToTerms;
        this.isAgreedToCollectPersonalInfo = isAgreedToCollectPersonalInfo;
        this.isAgreedToReceiveMarketingInfo = isAgreedToReceiveMarketingInfo;
    }

    /**
     * get method
     */
    public Integer getAge() {
        return Period.between(this.birthday, LocalDate.now()).getYears();
    }

    public Integer getGenderCode() {
        int genderCode = this.gender == Gender.MALE ? 1 : 2;
        if (this.birthday.getYear() >= 2000) {
            genderCode += 2;
        }
        return genderCode;
    }

    /**
     * update method
     * */
    public static AssociationMember create(
            String name,
            String nickname,
            LocalDate birthday,
            Gender gender,
            String phoneNumber,
            NursingInstitution institution,
            InstitutionRank institutionRank,
            Association association,
            AssociationRank associationRank,
            boolean isAgreedToReceiveMarketingInfo) {
        return AssociationMember.builder()
                .name(name)
                .nickname(nickname)
                .birthday(birthday)
                .gender(gender)
                .phoneNumber(phoneNumber)
                .institution(institution)
                .institutionRank(institutionRank)
                .association(association)
                .associationRank(associationRank)
                .isAgreedToReceiveMarketingInfo(isAgreedToReceiveMarketingInfo)
                .isAgreedToTerms(true)
                .isAgreedToCollectPersonalInfo(true)
                .build();
    }

    /*public void update(
            SocialWorkerProfileUpdateRequest request,
            LocalDate birthday,
            Gender gender,
            NursingInstitution nursingInstitution) {
        this.name = request.realName();
        this.nickname = request.nickName();
        this.birthday = birthday;
        this.gender = gender;
        this.phoneNumber = request.phoneNumber();
        this.nursingInstitution = nursingInstitution;
        this.institutionRank = request.institutionRank();
        this.isAgreedToReceiveMarketingInfo = request.isAgreedToReceiveMarketingInfo();
        this.isAgreedToTerms = request.isAgreedToTerms();
        this.isAgreedToCollectPersonalInfo = request.isAgreedToCollectPersonalInfo();
    }*/

    public void updateAssociationRank(AssociationRank rank) {
        this.associationRank = rank;
    }
}
