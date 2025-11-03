package com.becareful.becarefulserver.domain.association.domain;

import static com.becareful.becarefulserver.global.exception.ErrorMessage.ASSOCIATION_NOT_ACCESSABLE_OTHER_ASSOCIATION;

import com.becareful.becarefulserver.domain.common.domain.BaseEntity;
import com.becareful.becarefulserver.domain.common.domain.Gender;
import com.becareful.becarefulserver.domain.nursing_institution.domain.NursingInstitution;
import com.becareful.becarefulserver.domain.nursing_institution.domain.vo.InstitutionRank;
import com.becareful.becarefulserver.domain.socialworker.domain.SocialWorker;
import com.becareful.becarefulserver.domain.socialworker.domain.vo.AssociationRank;
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

    public static AssociationMember createMember(
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
                .isAgreedToTerms(isAgreedToTerms)
                .isAgreedToCollectPersonalInfo(isAgreedToCollectPersonalInfo)
                .isAgreedToReceiveMarketingInfo(isAgreedToReceiveMarketingInfo)
                .build();
    }

    public static AssociationMember createChairman(
            SocialWorker chairman,
            Association association,
            Boolean isAgreedToTerms,
            Boolean isAgreedToCollectPersonalInfo,
            Boolean isAgreedToReceiveMarketingInfo) {
        return AssociationMember.builder()
                .name(chairman.getName())
                .nickname(chairman.getNickname())
                .birthday(chairman.getBirthday())
                .gender(chairman.getGender())
                .phoneNumber(chairman.getPhoneNumber())
                .institution(chairman.getNursingInstitution())
                .institutionRank(chairman.getInstitutionRank())
                .association(association)
                .associationRank(AssociationRank.CHAIRMAN)
                .isAgreedToTerms(isAgreedToTerms)
                .isAgreedToCollectPersonalInfo(isAgreedToCollectPersonalInfo)
                .isAgreedToReceiveMarketingInfo(isAgreedToReceiveMarketingInfo)
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

    /**
     * update method
     * */
    public void updateAssociationRank(AssociationRank rank) {
        this.associationRank = rank;
    }

    public void validateAssociation(Association association) {
        if (this.association != association) {
            throw new DomainException(ASSOCIATION_NOT_ACCESSABLE_OTHER_ASSOCIATION);
        }
    }
}
