package com.becareful.becarefulserver.domain.socialworker.domain;

import com.becareful.becarefulserver.domain.association.domain.Association;
import com.becareful.becarefulserver.domain.common.domain.BaseEntity;
import com.becareful.becarefulserver.domain.common.vo.Gender;
import com.becareful.becarefulserver.domain.nursingInstitution.domain.NursingInstitution;
import com.becareful.becarefulserver.domain.nursingInstitution.vo.InstitutionRank;
import com.becareful.becarefulserver.domain.socialworker.domain.vo.AssociationRank;
import jakarta.persistence.*;
import java.time.LocalDate;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SocialWorker extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "social_worker_id")
    private Long id;

    private String name;

    private String nickname;

    private LocalDate birthday; // YYYYMMDD

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
    private SocialWorker(
            NursingInstitution nursingInstitution,
            Association association,
            String name,
            String nickname,
            LocalDate birthday,
            Gender gender,
            String phoneNumber,
            InstitutionRank institutionRank,
            AssociationRank associationRank,
            boolean isAgreedToTerms,
            boolean isAgreedToCollectPersonalInfo,
            boolean isAgreedToReceiveMarketingInfo) {
        this.association = association;
        this.name = name;
        this.nickname = nickname;
        this.birthday = birthday;
        this.gender = gender;
        this.phoneNumber = phoneNumber;
        this.institutionRank = institutionRank;
        this.associationRank = associationRank;
        this.isAgreedToTerms = isAgreedToTerms;
        this.nursingInstitution = nursingInstitution;
        this.isAgreedToCollectPersonalInfo = isAgreedToCollectPersonalInfo;
        this.isAgreedToReceiveMarketingInfo = isAgreedToReceiveMarketingInfo;
    }

    public static SocialWorker create(
            String name,
            String nickname,
            LocalDate birthday,
            Gender gender,
            String phoneNumber,
            InstitutionRank institutionRank,
            AssociationRank associationRank,
            boolean isAgreedToReceiveMarketingInfo,
            NursingInstitution nursingInstitution,
            Association association) {
        return SocialWorker.builder()
                .name(name)
                .nickname(nickname)
                .birthday(birthday)
                .gender(gender)
                .phoneNumber(phoneNumber)
                .institutionRank(institutionRank)
                .associationRank(associationRank)
                .isAgreedToReceiveMarketingInfo(isAgreedToReceiveMarketingInfo)
                .isAgreedToTerms(true)
                .isAgreedToCollectPersonalInfo(true)
                .nursingInstitution(nursingInstitution)
                .association(association)
                .build();
    }
}
