package com.becareful.becarefulserver.domain.recruitment.domain;

import static com.becareful.becarefulserver.global.exception.ErrorMessage.MATCHING_CANNOT_APPLY;
import static com.becareful.becarefulserver.global.exception.ErrorMessage.MATCHING_CANNOT_REJECT;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

import com.becareful.becarefulserver.domain.caregiver.domain.WorkApplication;
import com.becareful.becarefulserver.domain.common.domain.BaseEntity;
import com.becareful.becarefulserver.domain.recruitment.domain.converter.MediationTypeSetConverter;
import com.becareful.becarefulserver.domain.recruitment.dto.request.RecruitmentMediateRequest;
import com.becareful.becarefulserver.global.exception.exception.RecruitmentException;

import java.time.LocalDate;
import java.util.EnumSet;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Matching extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "matching_id")
    private Long id;

    @Enumerated(EnumType.STRING)
    private MatchingStatus matchingStatus;

    private LocalDate applicationDate;

    @Convert(converter = MediationTypeSetConverter.class)
    private EnumSet<MediationType> mediationTypes;

    private String mediationDescription;

    @JoinColumn(name = "recruitment_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private Recruitment recruitment;

    @JoinColumn(name = "work_application_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private WorkApplication workApplication;

    @Builder(access = AccessLevel.PRIVATE)
    private Matching(MatchingStatus matchingStatus, Recruitment recruitment,
            WorkApplication workApplication) {
        this.matchingStatus = matchingStatus;
        this.recruitment = recruitment;
        this.workApplication = workApplication;
    }

    public static Matching create(Recruitment recruitment, WorkApplication application) {
        return Matching.builder()
                .matchingStatus(MatchingStatus.미지원)
                .recruitment(recruitment)
                .workApplication(application)
                .build();
    }

    /**
     * Entity Method
     */

    public void apply() {
        validateMatchingUpdatable();
        this.matchingStatus = MatchingStatus.지원;
        this.applicationDate = LocalDate.now();
    }

    public void reject() {
        validateMatchingUpdatable();
        this.matchingStatus = MatchingStatus.거절;
    }

    public void mediate(RecruitmentMediateRequest request) {
        validateMatchingUpdatable();
        this.matchingStatus = MatchingStatus.지원;
        this.applicationDate = LocalDate.now();
        this.mediationTypes = EnumSet.copyOf(request.mediationTypes());
        this.mediationDescription = request.mediationDescription();
    }

    private void validateMatchingUpdatable() {
        if (!this.matchingStatus.equals(MatchingStatus.미지원)) {
            throw new RecruitmentException(MATCHING_CANNOT_REJECT);
        }
    }
}
