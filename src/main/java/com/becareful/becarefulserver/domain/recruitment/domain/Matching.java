package com.becareful.becarefulserver.domain.recruitment.domain;

import static com.becareful.becarefulserver.global.exception.ErrorMessage.MATCHING_CANNOT_APPLY;
import static com.becareful.becarefulserver.global.exception.ErrorMessage.MATCHING_CANNOT_REJECT;

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

import com.becareful.becarefulserver.domain.caregiver.domain.WorkApplication;
import com.becareful.becarefulserver.domain.common.domain.BaseEntity;
import com.becareful.becarefulserver.domain.recruitment.domain.converter.MediationTypeSetConverter;
import com.becareful.becarefulserver.domain.recruitment.domain.vo.MatchingInfo;
import com.becareful.becarefulserver.domain.recruitment.dto.request.RecruitmentMediateRequest;
import com.becareful.becarefulserver.global.exception.exception.RecruitmentException;

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
    private MatchingStatus matchingStatus;

    private LocalDate applicationDate;

    @Convert(converter = MediationTypeSetConverter.class)
    private EnumSet<MediationType> mediationTypes;

    private String mediationDescription;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "workDayMatchingRate", column = @Column(name = "caregiver_work_day_matching_rate")),
            @AttributeOverride(name = "workTimeMatchingRate", column = @Column(name = "caregiver_work_time_matching_rate")),
            @AttributeOverride(name = "workLocationMatchingRate", column = @Column(name = "caregiver_work_location_matching_rate")),
            @AttributeOverride(name = "workCareTypeMatchingRate", column = @Column(name = "caregiver_work_care_type_matching_rate")),
            @AttributeOverride(name = "workSalaryMatchingRate", column = @Column(name = "caregiver_work_salary_matching_rate")),
            @AttributeOverride(name = "workSalaryDifference", column = @Column(name = "caregiver_salary_difference_amount"))
    })
    private MatchingInfo caregiverMatchingInfo;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "workDayMatchingRate", column = @Column(name = "social_worker_work_day_matching_rate")),
            @AttributeOverride(name = "workTimeMatchingRate", column = @Column(name = "social_worker_work_time_matching_rate")),
            @AttributeOverride(name = "workLocationMatchingRate", column = @Column(name = "social_worker_work_location_matching_rate")),
            @AttributeOverride(name = "workCareTypeMatchingRate", column = @Column(name = "social_worker_work_care_type_matching_rate")),
            @AttributeOverride(name = "workSalaryMatchingRate", column = @Column(name = "social_worker_work_salary_matching_rate")),
            @AttributeOverride(name = "workSalaryDifference", column = @Column(name = "social_worker_salary_difference_amount"))
    })
    private MatchingInfo socialWorkerMatchingInfo;

    @JoinColumn(name = "recruitment_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private Recruitment recruitment;

    @JoinColumn(name = "work_application_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private WorkApplication workApplication;

    @Builder(access = AccessLevel.PRIVATE)
    private Matching(MatchingStatus matchingStatus, Recruitment recruitment,
            WorkApplication workApplication, MatchingInfo caregiverMatchingInfo, MatchingInfo socialWorkerMatchingInfo) {
        this.matchingStatus = matchingStatus;
        this.recruitment = recruitment;
        this.workApplication = workApplication;
        this.caregiverMatchingInfo = caregiverMatchingInfo;
        this.socialWorkerMatchingInfo = socialWorkerMatchingInfo;
    }

    public static Matching create(Recruitment recruitment, WorkApplication application, MatchingInfo caregiverMatchingInfo, MatchingInfo socialWorkerMatchingInfo) {
        return Matching.builder()
                .matchingStatus(MatchingStatus.미지원)
                .recruitment(recruitment)
                .workApplication(application)
                .caregiverMatchingInfo(caregiverMatchingInfo)
                .socialWorkerMatchingInfo(socialWorkerMatchingInfo)
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

    public void hire() {
        validateMatchingCompletable();
        this.matchingStatus = MatchingStatus.합격;
    }

    public void failed() {
        validateMatchingCompletable();
        this.matchingStatus = MatchingStatus.불합격;
    }

    private void validateMatchingCompletable() {
        if (!this.matchingStatus.equals(MatchingStatus.지원)) {
            throw new RecruitmentException("지원한 경우에만 합격, 불합격 처리할 수 있습니다.");
        }
    }

    private void validateMatchingUpdatable() {
        if (!this.matchingStatus.equals(MatchingStatus.미지원)) {
            throw new RecruitmentException(MATCHING_CANNOT_REJECT);
        }
    }
}
