package com.becareful.becarefulserver.domain.socialworker.domain;


import com.becareful.becarefulserver.domain.association.domain.Association;
import com.becareful.becarefulserver.domain.common.domain.BaseEntity;
import com.becareful.becarefulserver.domain.common.vo.Gender;
import com.becareful.becarefulserver.domain.nursingInstitution.domain.NursingInstitution;
import com.becareful.becarefulserver.domain.socialworker.domain.vo.AssociationRank;
import com.becareful.becarefulserver.domain.nursingInstitution.vo.InstitutionRank;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Socialworker extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String realName;

    private String nickName;

    private LocalDate birthday; //YYYYMMDD

    @Enumerated(EnumType.STRING)
    private Gender gender;

    private String phoneNumber;

    private String password;

    @Enumerated(EnumType.STRING)
    private InstitutionRank institutionRank;

    @Enumerated(EnumType.STRING)
    private AssociationRank associationRank;

    private boolean isAgreedToTerms;

    private boolean isAgreedToCollectPersonalInfo;

    private boolean isAgreedToReceiveMarketingInfo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "nursing_institution_id")
    private NursingInstitution nursingInstitution;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "association_id")
    private Association association;


    @Builder(access = AccessLevel.PRIVATE)
    private Socialworker( NursingInstitution nursingInstitution, Association association, String realName, String nickName, LocalDate birthday, Gender gender, String phoneNumber,
                         InstitutionRank institutionRank, AssociationRank associationRank,
                         boolean isAgreedToTerms, boolean isAgreedToCollectPersonalInfo,
                         boolean isAgreedToReceiveMarketingInfo) {
        this.nursingInstitution = nursingInstitution;
        this.association = association;
        this.realName = realName;
        this.nickName = nickName;
        this.birthday = birthday;
        this.gender = gender;
        this.phoneNumber = phoneNumber;
        this.institutionRank = institutionRank;
        this.associationRank = associationRank;
        this.isAgreedToTerms = isAgreedToTerms;
        this.isAgreedToCollectPersonalInfo = isAgreedToCollectPersonalInfo;
        this.isAgreedToReceiveMarketingInfo = isAgreedToReceiveMarketingInfo;
    }

    public static Socialworker create(NursingInstitution nursingInstitution, Association association, String realName, String nickName, LocalDate birthday, Gender gender, String phoneNumber,
                                      InstitutionRank institutionRank, AssociationRank associationRank,
                                      boolean isAgreedToReceiveMarketingInfo) {
        return Socialworker.builder()
                .nursingInstitution(nursingInstitution)
                .association(association)
                .realName(realName)
                .nickName(nickName)
                .birthday(birthday)
                .gender(gender)
                .phoneNumber(phoneNumber)
                .institutionRank(institutionRank)
                .associationRank(associationRank)
                .isAgreedToReceiveMarketingInfo(isAgreedToReceiveMarketingInfo)
                .isAgreedToTerms(true)
                .isAgreedToCollectPersonalInfo(true)
                .build();
    }

}
