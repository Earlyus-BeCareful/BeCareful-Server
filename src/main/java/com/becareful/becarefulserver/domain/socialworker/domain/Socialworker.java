package com.becareful.becarefulserver.domain.socialworker.domain;


import com.becareful.becarefulserver.domain.socialworker.domain.vo.Rank;
import com.becareful.becarefulserver.domain.common.vo.Gender;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Socialworker{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    @Enumerated(EnumType.STRING)
    private Gender gender;

    private String phoneNumber;
    private String password;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "nursing_institution_id")
    private NursingInstitution nursingInstitution;

    @Enumerated(EnumType.STRING)
    private Rank institutionRank;

    private boolean isAgreedToTerms;
    private boolean isAgreedToCollectPersonalInfo;
    private boolean isAgreedToReceiveMarketingInfo;


    @Builder(access = AccessLevel.PRIVATE)
    private Socialworker(String name, Gender gender, String phoneNumber, String password,
                         NursingInstitution nursingInstitution,  Rank rank,
                         boolean isAgreedToTerms, boolean isAgreedToCollectPersonalInfo,
                        boolean isAgreedToReceiveMarketingInfo) {
        this.name = name;
        this.gender = gender;
        this.phoneNumber = phoneNumber;
        this.password = password;
        this.nursingInstitution = nursingInstitution;
        this.institutionRank = rank;
        this.isAgreedToTerms = isAgreedToTerms;
        this.isAgreedToCollectPersonalInfo = isAgreedToCollectPersonalInfo;
        this.isAgreedToReceiveMarketingInfo = isAgreedToReceiveMarketingInfo;
    }

    public static Socialworker create(String name, Gender gender, String phoneNumber, String encodedPassword,
                                      NursingInstitution institution, Rank rank,
                                      boolean isAgreedToReceiveMarketingInfo) {
        return Socialworker.builder()
                .name(name)
                .gender(gender)
                .phoneNumber(phoneNumber)
                .password(encodedPassword)
                .nursingInstitution(institution)
                .rank(rank)
                .isAgreedToReceiveMarketingInfo(isAgreedToReceiveMarketingInfo)
                .isAgreedToTerms(true)
                .isAgreedToCollectPersonalInfo(true)
                .build();
    }


}
