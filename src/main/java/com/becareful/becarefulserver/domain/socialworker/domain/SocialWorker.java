package com.becareful.becarefulserver.domain.socialworker.domain;

import com.becareful.becarefulserver.domain.association.domain.AssociationMember;
import com.becareful.becarefulserver.domain.common.domain.BaseEntity;
import com.becareful.becarefulserver.domain.common.domain.Gender;
import com.becareful.becarefulserver.domain.nursing_institution.domain.NursingInstitution;
import com.becareful.becarefulserver.domain.nursing_institution.domain.vo.InstitutionRank;
import com.becareful.becarefulserver.domain.socialworker.dto.request.SocialWorkerProfileUpdateRequest;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.Period;
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

    private LocalDate birthday;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    private String phoneNumber;

    @Enumerated(EnumType.STRING)
    private InstitutionRank institutionRank;

    private boolean isAgreedToTerms;

    private boolean isAgreedToCollectPersonalInfo;

    private boolean isAgreedToReceiveMarketingInfo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "nursing_institution_id", nullable = false)
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
        this.phoneNumber = request.phoneNumber();
        this.nursingInstitution = nursingInstitution;
        this.institutionRank = request.institutionRank();
        this.isAgreedToReceiveMarketingInfo = request.isAgreedToReceiveMarketingInfo();
        this.isAgreedToTerms = request.isAgreedToTerms();
        this.isAgreedToCollectPersonalInfo = request.isAgreedToCollectPersonalInfo();
    }
}
