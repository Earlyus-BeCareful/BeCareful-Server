package com.becareful.becarefulserver.domain.socialworker.domain;

import com.becareful.becarefulserver.domain.association.domain.Association;
import com.becareful.becarefulserver.domain.common.domain.BaseEntity;
import com.becareful.becarefulserver.domain.common.vo.Gender;
import com.becareful.becarefulserver.domain.nursing_institution.domain.NursingInstitution;
import com.becareful.becarefulserver.domain.nursing_institution.vo.InstitutionRank;
import com.becareful.becarefulserver.domain.socialworker.domain.vo.AssociationRank;
import com.becareful.becarefulserver.domain.socialworker.dto.request.SocialWorkerUpdateBasicInfoRequest;
import jakarta.persistence.*;
import java.time.LocalDate;
import lombok.*;

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

    @Setter(AccessLevel.PUBLIC)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "association_id")
    private Association association;

    @Builder(access = AccessLevel.PRIVATE)
    private SocialWorker(
            NursingInstitution nursingInstitution,
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
            NursingInstitution nursingInstitution) {
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
                .build();
    }

    public void joinAssociation(Association association, AssociationRank rank) {
        this.association = association;
        this.associationRank = rank;
    }

    public void leaveAssociation() {
        this.association = null;
        this.associationRank = AssociationRank.NONE;
    }

    public void updateBasicInfo(
            SocialWorkerUpdateBasicInfoRequest request,
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
    }

    public void updateAssociationRank(AssociationRank rank) {
        this.associationRank = rank;
    }
}
