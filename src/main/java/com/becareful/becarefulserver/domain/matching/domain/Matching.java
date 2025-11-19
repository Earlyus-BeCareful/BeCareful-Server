package com.becareful.becarefulserver.domain.matching.domain;

import static com.becareful.becarefulserver.global.exception.ErrorMessage.*;

import com.becareful.becarefulserver.domain.caregiver.domain.WorkApplication;
import com.becareful.becarefulserver.domain.common.domain.BaseEntity;
import com.becareful.becarefulserver.domain.matching.domain.converter.MediationTypeSetConverter;
import com.becareful.becarefulserver.domain.matching.domain.vo.MatchingResultInfo;
import com.becareful.becarefulserver.global.exception.exception.MatchingException;
import com.becareful.becarefulserver.global.exception.exception.RecruitmentException;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.EnumSet;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(
        uniqueConstraints = {
            @UniqueConstraint(
                    name = "uk_matching_application_recruitment",
                    columnNames = {"work_application_id", "recruitment_id"})
        })
public class Matching extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "matching_id")
    private Long id;

    @Enumerated(EnumType.STRING)
    private MatchingStatus matchingStatus;

    boolean isPending;

    @Enumerated(EnumType.STRING)
    private MatchingApplicationStatus applicationStatus;

    private LocalDate applicationDate;

    @Convert(converter = MediationTypeSetConverter.class)
    private EnumSet<MediationType> mediationTypes;

    private String mediationDescription;

    @Embedded
    private MatchingResultInfo matchingResultInfo;

    @JoinColumn(name = "recruitment_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Recruitment recruitment;

    @JoinColumn(name = "work_application_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private WorkApplication workApplication;

    @Builder(access = AccessLevel.PRIVATE)
    private Matching(
            MatchingStatus matchingStatus,
            MatchingApplicationStatus applicationStatus,
            Recruitment recruitment,
            WorkApplication workApplication,
            MatchingResultInfo matchingResultInfo) {
        this.matchingStatus = matchingStatus;
        this.isPending = false;
        this.applicationStatus = applicationStatus;
        this.recruitment = recruitment;
        this.workApplication = workApplication;
        this.matchingResultInfo = matchingResultInfo;
    }

    public static Matching create(Recruitment recruitment, WorkApplication application) {
        return Matching.builder()
                .applicationStatus(MatchingApplicationStatus.미지원)
                .matchingStatus(MatchingStatus.미지원)
                .recruitment(recruitment)
                .workApplication(application)
                .build();
    }

    /**
     * Get Method
     */
    public boolean isApplicationReviewing() {
        return matchingStatus.equals(MatchingStatus.지원검토);
    }

    /**
     * Entity Method
     */
    public void propose() {
        validateCanPropose();
        this.matchingStatus = MatchingStatus.근무제안;
    }

    public void setPending() {
        validateCanSetPending();
        this.isPending = true;
    }

    public void unsetPending() {
        validateCanUnsetPending();
        this.isPending = false;
    }

    public void confirm() {
        this.matchingStatus = MatchingStatus.채용완료;
        this.recruitment.complete();
    }

    public void failedConfirm() {
        this.matchingStatus = MatchingStatus.채용불발;
    }

    private void validateCanSetPending() {
        if (isPending) {
            throw new RecruitmentException(MATCHING_ALREADY_PENDING);
        }
    }

    private void validateCanUnsetPending() {
        if (!isPending) {
            throw new RecruitmentException(MATCHING_ALREADY_NOT_PENDING);
        }
    }

    private void validateCanPropose() {
        if (isPending) {
            throw new MatchingException(MATCHING_CANNOT_PROPOSE_PENDING_MATCHING);
        }
    }

    private void validateMatchingApplicable() {
        if (matchingStatus.equals(MatchingStatus.미지원) && applicationStatus.equals(MatchingApplicationStatus.미지원)) {
            return;
        }
        throw new RecruitmentException(MATCHING_CANNOT_REJECT);
    }
}
