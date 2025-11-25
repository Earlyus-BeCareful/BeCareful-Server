package com.becareful.becarefulserver.domain.socialworker.domain;

import static com.becareful.becarefulserver.global.exception.ErrorMessage.ASSOCIATION_MEMBER_ALREADY_LEAVED;

import com.becareful.becarefulserver.domain.association.domain.AssociationMember;
import com.becareful.becarefulserver.domain.association.domain.AssociationRank;
import com.becareful.becarefulserver.domain.common.domain.BaseEntity;
import com.becareful.becarefulserver.domain.common.domain.Gender;
import com.becareful.becarefulserver.domain.nursing_institution.domain.NursingInstitution;
import com.becareful.becarefulserver.domain.nursing_institution.domain.vo.InstitutionRank;
import com.becareful.becarefulserver.domain.socialworker.dto.request.SocialWorkerProfileUpdateRequest;
import com.becareful.becarefulserver.global.exception.exception.DomainException;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLDelete(
        sql =
                """
    UPDATE social_worker
       SET is_deleted = true,
           delete_date = now(),
           name = null,
           nickname = null,
           birthday = null,
           gender = null,
           phone_number = null
     WHERE social_worker_id = ?
""")
@SQLRestriction("is_deleted = false")
public class SocialWorker extends BaseEntity {

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

    private boolean isDeleted;

    private LocalDateTime deleteDate;

    private boolean isAgreedToTerms;

    private boolean isAgreedToCollectPersonalInfo;

    private boolean isAgreedToReceiveMarketingInfo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "nursing_institution_id")
    private NursingInstitution nursingInstitution;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "association_member_id")
    private AssociationMember associationMember;

    @Builder(access = AccessLevel.PRIVATE)
    private SocialWorker(
            NursingInstitution nursingInstitution,
            String name,
            String nickname,
            LocalDate birthday,
            Gender gender,
            String phoneNumber,
            InstitutionRank institutionRank,
            boolean isAgreedToTerms,
            boolean isAgreedToCollectPersonalInfo,
            boolean isAgreedToReceiveMarketingInfo) {
        this.name = name;
        this.nickname = nickname;
        this.birthday = birthday;
        this.gender = gender;
        this.phoneNumber = phoneNumber;
        this.institutionRank = institutionRank;
        this.isDeleted = false;
        this.isAgreedToTerms = isAgreedToTerms;
        this.nursingInstitution = nursingInstitution;
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

    public AssociationRank getAssociationRank() {
        if (this.associationMember == null) {
            return AssociationRank.NONE;
        }
        return this.associationMember.getAssociationRank();
    }

    /**
     * update method
     * */
    public static SocialWorker create(
            String name,
            String nickname,
            LocalDate birthday,
            Gender gender,
            String phoneNumber,
            InstitutionRank institutionRank,
            boolean isAgreedToReceiveMarketingInfo,
            NursingInstitution nursingInstitution) {
        return SocialWorker.builder()
                .name(name)
                .nickname(nickname)
                .birthday(birthday)
                .gender(gender)
                .phoneNumber(phoneNumber)
                .institutionRank(institutionRank)
                .isAgreedToReceiveMarketingInfo(isAgreedToReceiveMarketingInfo)
                .isAgreedToTerms(true)
                .isAgreedToCollectPersonalInfo(true)
                .nursingInstitution(nursingInstitution)
                .build();
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
        this.nursingInstitution = nursingInstitution;
        this.institutionRank = request.institutionRank();
        this.isAgreedToReceiveMarketingInfo = request.isAgreedToReceiveMarketingInfo();
        this.isAgreedToTerms = request.isAgreedToTerms();
        this.isAgreedToCollectPersonalInfo = request.isAgreedToCollectPersonalInfo();

        if (this.associationMember != null) {
            this.associationMember.update(request, birthday, gender, nursingInstitution);
        }
    }

    public void joinAssociation(AssociationMember member) {
        this.associationMember = member;
    }

    public void leaveAssociation() {
        if (this.associationMember == null) {
            throw new DomainException(ASSOCIATION_MEMBER_ALREADY_LEAVED);
        }
        this.associationMember.leaveAssociation();
        this.associationMember = null;
    }
}
