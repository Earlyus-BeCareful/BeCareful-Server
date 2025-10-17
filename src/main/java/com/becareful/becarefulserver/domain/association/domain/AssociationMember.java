package com.becareful.becarefulserver.domain.association.domain;

import com.becareful.becarefulserver.domain.association.domain.vo.AssociationRank;
import com.becareful.becarefulserver.domain.common.domain.BaseEntity;
import com.becareful.becarefulserver.domain.common.domain.Gender;
import com.becareful.becarefulserver.domain.nursing_institution.domain.NursingInstitution;
import com.becareful.becarefulserver.domain.nursing_institution.domain.vo.InstitutionRank;
import com.becareful.becarefulserver.domain.socialworker.domain.SocialWorker;
import jakarta.persistence.*;
import java.time.LocalDate;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AssociationMember extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "association_member_id")
    private Long id;

    @Column(unique = true, nullable = false)
    private String phoneNumber;

    private String name;

    private String nickname;

    private LocalDate birthday;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    private boolean isAgreedToTerms;

    private boolean isAgreedToCollectPersonalInfo;

    private boolean isAgreedToReceiveMarketingInfo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "nursing_institution_id")
    private NursingInstitution nursingInstitution;

    @Enumerated(EnumType.STRING)
    private InstitutionRank institutionRank;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "association_id")
    private Association association;

    @Enumerated(EnumType.STRING)
    private AssociationRank associationRank;

    @Builder(access = AccessLevel.PRIVATE)
    private AssociationMember(
            String phoneNumber,
            String name,
            String nickname,
            LocalDate birthday,
            Gender gender,
            boolean isAgreedToTerms,
            boolean isAgreedToCollectPersonalInfo,
            boolean isAgreedToReceiveMarketingInfo,
            NursingInstitution nursingInstitution,
            InstitutionRank institutionRank,
            Association association,
            AssociationRank associationRank) {
        this.phoneNumber = phoneNumber;
        this.name = name;
        this.nickname = nickname;
        this.birthday = birthday;
        this.gender = gender;
        this.isAgreedToTerms = isAgreedToTerms;
        this.isAgreedToCollectPersonalInfo = isAgreedToCollectPersonalInfo;
        this.isAgreedToReceiveMarketingInfo = isAgreedToReceiveMarketingInfo;
        this.nursingInstitution = nursingInstitution;
        this.institutionRank = institutionRank;
        this.association = association;
        this.associationRank = associationRank;
    }

    public static AssociationMember create(
            SocialWorker socialWorker,
            Association association,
            AssociationRank associationRank,
            boolean isAgreedToTerms,
            boolean isAgreedToCollectPersonalInfo,
            boolean isAgreedToReceiveMarketingInfo) {
        return AssociationMember.builder()
                .phoneNumber(socialWorker.getPhoneNumber())
                .name(socialWorker.getName())
                .nickname(socialWorker.getNickname())
                .birthday(socialWorker.getBirthday())
                .gender(socialWorker.getGender())
                .nursingInstitution(socialWorker.getNursingInstitution())
                .institutionRank(socialWorker.getInstitutionRank())
                .association(association)
                .associationRank(associationRank)
                .isAgreedToTerms(isAgreedToTerms)
                .isAgreedToCollectPersonalInfo(isAgreedToCollectPersonalInfo)
                .isAgreedToReceiveMarketingInfo(isAgreedToReceiveMarketingInfo)
                .build();
    }

    /**
     * update method
     */
    public void leaveAssociation() {
        this.association = null;
        this.associationRank = AssociationRank.NONE;
    }

    public void updateAssociationRank(AssociationRank rank) {
        this.associationRank = rank;
    }
}
