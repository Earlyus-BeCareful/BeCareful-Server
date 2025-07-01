package com.becareful.becarefulserver.domain.matching.domain;

import static com.becareful.becarefulserver.global.exception.ErrorMessage.MATCHING_CANNOT_REJECT;

import com.becareful.becarefulserver.domain.caregiver.domain.WorkApplication;
import com.becareful.becarefulserver.domain.common.domain.BaseEntity;
import com.becareful.becarefulserver.domain.matching.domain.converter.MediationTypeSetConverter;
import com.becareful.becarefulserver.domain.matching.domain.vo.MatchingResultInfo;
import com.becareful.becarefulserver.domain.matching.dto.request.RecruitmentMediateRequest;
import com.becareful.becarefulserver.global.exception.exception.RecruitmentException;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
public class Matching extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "matching_id")
    private Long id;

    @Enumerated(EnumType.STRING)
    private MatchingApplicationStatus matchingApplicationStatus;

    private LocalDate applicationDate;

    @Convert(converter = MediationTypeSetConverter.class)
    private EnumSet<MediationType> mediationTypes;

    private String mediationDescription;

    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "workDayMatchingRate", column = @Column(name = "caregiver_work_day_matching_rate")),
        @AttributeOverride(name = "isWorkTimeMatched", column = @Column(name = "caregiver_is_work_time_matched")),
        @AttributeOverride(
                name = "isWorkLocationMatched",
                column = @Column(name = "caregiver_is_work_location_matched"))
    })
    private MatchingResultInfo caregiverMatchingResultInfo;

    @Embedded
    @AttributeOverrides({
        @AttributeOverride(
                name = "workDayMatchingRate",
                column = @Column(name = "social_worker_work_day_matching_rate")),
        @AttributeOverride(name = "isWorkTimeMatched", column = @Column(name = "social_worker_is_work_time_matched")),
        @AttributeOverride(
                name = "isWorkLocationMatched",
                column = @Column(name = "social_worker_is_work_location_matched"))
    })
    private MatchingResultInfo socialWorkerMatchingResultInfo;

    @JoinColumn(name = "recruitment_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private Recruitment recruitment;

    @JoinColumn(name = "work_application_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private WorkApplication workApplication;

    @Builder(access = AccessLevel.PRIVATE)
    private Matching(
            MatchingApplicationStatus matchingApplicationStatus,
            Recruitment recruitment,
            WorkApplication workApplication,
            MatchingResultInfo caregiverMatchingResultInfo,
            MatchingResultInfo socialWorkerMatchingResultInfo) {
        this.matchingApplicationStatus = matchingApplicationStatus;
        this.recruitment = recruitment;
        this.workApplication = workApplication;
        this.caregiverMatchingResultInfo = caregiverMatchingResultInfo;
        this.socialWorkerMatchingResultInfo = socialWorkerMatchingResultInfo;
    }

    public static Matching create(
            Recruitment recruitment,
            WorkApplication application,
            MatchingResultInfo caregiverMatchingResultInfo,
            MatchingResultInfo socialWorkerMatchingResultInfo) {
        return Matching.builder()
                .matchingApplicationStatus(MatchingApplicationStatus.미지원)
                .recruitment(recruitment)
                .workApplication(application)
                .caregiverMatchingResultInfo(caregiverMatchingResultInfo)
                .socialWorkerMatchingResultInfo(socialWorkerMatchingResultInfo)
                .build();
    }

    /**
     * Entity Method
     */
    public void apply() {
        validateMatchingUpdatable();
        this.matchingApplicationStatus = MatchingApplicationStatus.지원;
        this.applicationDate = LocalDate.now();
    }

    public void reject() {
        validateMatchingUpdatable();
        this.matchingApplicationStatus = MatchingApplicationStatus.거절;
    }

    public void mediate(RecruitmentMediateRequest request) {
        validateMatchingUpdatable();
        this.matchingApplicationStatus = MatchingApplicationStatus.지원;
        this.applicationDate = LocalDate.now();
        this.mediationTypes = EnumSet.copyOf(request.mediationTypes());
        this.mediationDescription = request.mediationDescription();
    }

    public void hire() {
        validateMatchingCompletable();
        this.matchingApplicationStatus = MatchingApplicationStatus.합격;
    }

    public void failed() {
        validateMatchingCompletable();
        this.matchingApplicationStatus = MatchingApplicationStatus.불합격;
    }

    private void validateMatchingCompletable() {
        if (!this.matchingApplicationStatus.equals(MatchingApplicationStatus.지원)) {
            throw new RecruitmentException("지원한 경우에만 합격, 불합격 처리할 수 있습니다.");
        }
    }

    private void validateMatchingUpdatable() {
        if (!this.matchingApplicationStatus.equals(MatchingApplicationStatus.미지원)) {
            throw new RecruitmentException(MATCHING_CANNOT_REJECT);
        }
    }
}
