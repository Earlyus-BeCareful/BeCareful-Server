package com.becareful.becarefulserver.domain.association.domain;

import static com.becareful.becarefulserver.global.exception.ErrorMessage.ASSOCIATION_MEMBER_NOT_ENOUGH_RANK;
import static com.becareful.becarefulserver.global.exception.ErrorMessage.ASSOCIATION_NOT_ACCESSABLE_OTHER_ASSOCIATION;

import com.becareful.becarefulserver.domain.common.domain.BaseEntity;
import com.becareful.becarefulserver.domain.common.domain.Gender;
import com.becareful.becarefulserver.domain.community.domain.vo.CommunityAgreement;
import com.becareful.becarefulserver.domain.nursing_institution.domain.NursingInstitution;
import com.becareful.becarefulserver.domain.nursing_institution.domain.vo.InstitutionRank;
import com.becareful.becarefulserver.domain.socialworker.domain.SocialWorker;
import com.becareful.becarefulserver.domain.socialworker.dto.request.SocialWorkerProfileUpdateRequest;
import com.becareful.becarefulserver.global.exception.exception.DomainException;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.Period;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AssociationMember extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "association_member_id")
    private Long id;

    private String name;

    private String nickname;

    private LocalDate birthday;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    private String phoneNumber;

    private boolean isLeaved;

    @Enumerated(EnumType.STRING)
    private InstitutionRank institutionRank;

    @Enumerated(EnumType.STRING)
    private AssociationRank associationRank;

    @Embedded
    private CommunityAgreement communityAgreement;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "nursing_institution_id")
    private NursingInstitution institution;

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
            CommunityAgreement communityAgreement) {
        this.name = name;
        this.nickname = nickname;
        this.birthday = birthday;
        this.gender = gender;
        this.phoneNumber = phoneNumber;
        this.isLeaved = false;
        this.institutionRank = institutionRank;
        this.association = association;
        this.associationRank = associationRank;
        this.institution = institution;
        this.communityAgreement = communityAgreement;
    }

    /**
     * get method
     */
    public Integer getAge() {
        return Period.between(this.birthday, LocalDate.now()).getYears();
    }

    public static AssociationMember create(
            SocialWorker socialWorker,
            Association association,
            AssociationRank associationRank,
            Boolean isAgreedToTerms,
            Boolean isAgreedToCollectPersonalInfo,
            Boolean isAgreedToReceiveMarketingInfo) {
        return AssociationMember.builder()
                .name(socialWorker.getName())
                .nickname(socialWorker.getNickname())
                .birthday(socialWorker.getBirthday())
                .gender(socialWorker.getGender())
                .phoneNumber(socialWorker.getPhoneNumber())
                .institution(socialWorker.getNursingInstitution())
                .institutionRank(socialWorker.getInstitutionRank())
                .association(association)
                .associationRank(associationRank)
                .communityAgreement(CommunityAgreement.of(
                        isAgreedToTerms, isAgreedToCollectPersonalInfo, isAgreedToReceiveMarketingInfo))
                .build();
    }

    public static AssociationMember createChairman(
            SocialWorker chairman,
            Association association,
            Boolean isAgreedToTerms,
            Boolean isAgreedToCollectPersonalInfo,
            Boolean isAgreedToReceiveMarketingInfo) {
        return AssociationMember.create(
                chairman,
                association,
                AssociationRank.CHAIRMAN,
                isAgreedToTerms,
                isAgreedToCollectPersonalInfo,
                isAgreedToReceiveMarketingInfo);
    }

    public void update(
            SocialWorkerProfileUpdateRequest request,
            LocalDate birthday,
            Gender gender,
            NursingInstitution nursingInstitution) {
        this.name = request.realName();
        this.nickname = request.nickName();
        this.birthday = birthday;
        this.gender = gender;
        this.phoneNumber = request.phoneNumber();
        this.institution = nursingInstitution;
        this.institutionRank = request.institutionRank();
    }

    public void updateAgreement(boolean isAgreedToReceiveMarketingInfo) {
        this.communityAgreement = CommunityAgreement.of(
                this.communityAgreement.isAgreedToTerms(),
                this.communityAgreement.isAgreedToCollectPersonalInfo(),
                isAgreedToReceiveMarketingInfo);
    }

    /**
     * update method
     * */
    public void updateAssociationRank(AssociationRank rank) {
        this.associationRank = rank;
    }

    public void leaveAssociation() {
        this.isLeaved = true;
    }

    /**
     * validate method
     */
    public void validateAssociation(Association association) {
        if (this.association != association) {
            throw new DomainException(ASSOCIATION_NOT_ACCESSABLE_OTHER_ASSOCIATION);
        }
    }

    public void validateHasAssociationManagableRank() {
        if (associationRank.isLowerThan(AssociationRank.EXECUTIVE)) {
            throw new DomainException(ASSOCIATION_MEMBER_NOT_ENOUGH_RANK);
        }
    }

    public void validateChairman() {
        if (associationRank.isLowerThan(AssociationRank.CHAIRMAN)) {
            throw new DomainException(ASSOCIATION_MEMBER_NOT_ENOUGH_RANK);
        }
    }
}
