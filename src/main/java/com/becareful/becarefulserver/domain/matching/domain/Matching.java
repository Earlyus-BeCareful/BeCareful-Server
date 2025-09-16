package com.becareful.becarefulserver.domain.matching.domain;

import static com.becareful.becarefulserver.global.exception.ErrorMessage.*;

import com.becareful.becarefulserver.domain.caregiver.domain.WorkApplication;
import com.becareful.becarefulserver.domain.caregiver.domain.WorkTime;
import com.becareful.becarefulserver.domain.common.domain.BaseEntity;
import com.becareful.becarefulserver.domain.common.domain.vo.Location;
import com.becareful.becarefulserver.domain.matching.domain.converter.MediationTypeSetConverter;
import com.becareful.becarefulserver.domain.matching.domain.vo.MatchingResultInfo;
import com.becareful.becarefulserver.domain.matching.domain.vo.MatchingResultStatus;
import com.becareful.becarefulserver.domain.matching.dto.request.RecruitmentMediateRequest;
import com.becareful.becarefulserver.global.exception.exception.MatchingException;
import com.becareful.becarefulserver.global.exception.exception.RecruitmentException;
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
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.EnumSet;
import java.util.List;
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
    private MatchingResultInfo matchingResultInfo;

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
            MatchingResultInfo matchingResultInfo) {
        this.matchingApplicationStatus = matchingApplicationStatus;
        this.recruitment = recruitment;
        this.workApplication = workApplication;
        this.matchingResultInfo = matchingResultInfo;
    }

    public static Matching create(Recruitment recruitment, WorkApplication application, List<Location> locations) {
        return Matching.builder()
                .matchingApplicationStatus(MatchingApplicationStatus.미지원)
                .recruitment(recruitment)
                .workApplication(application)
                .matchingResultInfo(calculateMatchingRate(recruitment, application, locations))
                .build();
    }

    /**
     * Get Method
     */
    public boolean isApplicationReviewing() {
        return matchingApplicationStatus.equals(MatchingApplicationStatus.지원검토중);
    }

    public boolean isApplicationPassed() {
        return matchingApplicationStatus.equals(MatchingApplicationStatus.근무제안);
    }

    public boolean isApplicationRefused() {
        return matchingApplicationStatus.equals(MatchingApplicationStatus.지원거절);
    }

    /**
     * Entity Method
     */
    public void apply() {
        validateMatchingUpdatable();
        this.matchingApplicationStatus = MatchingApplicationStatus.지원검토중;
        this.applicationDate = LocalDate.now();
    }

    public void reject() {
        validateMatchingUpdatable();
        this.matchingApplicationStatus = MatchingApplicationStatus.매칭거부;
    }

    public void mediate(RecruitmentMediateRequest request) {
        validateMatchingUpdatable();
        this.matchingApplicationStatus = MatchingApplicationStatus.지원검토중;
        this.applicationDate = LocalDate.now();
        this.mediationTypes = EnumSet.copyOf(request.mediationTypes());
        this.mediationDescription = request.mediationDescription();
    }

    public void propose() {
        this.matchingApplicationStatus = MatchingApplicationStatus.근무제안;
    }

    public void failed() {
        validateMatchingFailable();
        this.matchingApplicationStatus = MatchingApplicationStatus.지원거절;
    }

    public MatchingResultStatus getMatchingResultStatus() {
        return matchingResultInfo.judgeMatchingResultStatus();
    }

    private void validateMatchingFailable() {
        if (matchingApplicationStatus.equals(MatchingApplicationStatus.지원검토중)) {
            return;
        }
        throw new RecruitmentException("지원한 경우에만 불합격 처리할 수 있습니다.");
    }

    private void validateMatchingUpdatable() {
        if (matchingApplicationStatus.equals(MatchingApplicationStatus.미지원)) {
            return;
        }
        throw new RecruitmentException(MATCHING_CANNOT_REJECT);
    }

    public void validateCaregiver(Long caregiverId) {
        if (workApplication.getCaregiver().getId().equals(caregiverId)) {
            return;
        }
        throw new MatchingException(MATCHING_CAREGIVER_DIFFERENT);
    }

    public void validateSocialWorker(Long socialWorkerId) {
        if (workApplication.getCaregiver().getId().equals(socialWorkerId)) {
            return;
        }
        throw new MatchingException(MATCHING_SOCIAL_WORKER_DIFFERENT);
    }

    /**
     * @param recruitment       - 사회복지사가 등록한 공고
     * @param workApplication   - 요양보호사가 등록한 지원서
     * @param locations         - 지원서에 등록된 희망 근무 장소
     * @return                  - MatchingInfo
     */
    private static MatchingResultInfo calculateMatchingRate(
            Recruitment recruitment, WorkApplication workApplication, List<Location> locations) {
        boolean workLocationMatchingRate = isWorkLocationMatched(recruitment.getResidentialLocation(), locations);
        Double workDayMatchingRate = calculateDayMatchingRate(recruitment.getWorkDays(), workApplication.getWorkDays());
        boolean workTimeMatchingRate = isWorkTimeMatched(recruitment.getWorkTimes(), workApplication.getWorkTimes());

        return MatchingResultInfo.create(workLocationMatchingRate, workDayMatchingRate, workTimeMatchingRate);
    }

    private static boolean isWorkLocationMatched(Location residentialLocation, List<Location> workableLocations) {
        for (Location location : workableLocations) {
            if (location.equals(residentialLocation)) {
                return true;
            }
        }
        return false;
    }

    private static Double calculateDayMatchingRate(EnumSet<DayOfWeek> recruitmentDays, EnumSet<DayOfWeek> applyDays) {
        EnumSet<DayOfWeek> intersection = EnumSet.copyOf(recruitmentDays);
        intersection.retainAll(applyDays);

        return ((double) intersection.size() / recruitmentDays.size()) * 100;
    }

    private static boolean isWorkTimeMatched(EnumSet<WorkTime> recruitmentTimes, EnumSet<WorkTime> applyTimes) {
        EnumSet<WorkTime> intersection = EnumSet.copyOf(recruitmentTimes);
        intersection.retainAll(applyTimes);
        return ((double) intersection.size() / recruitmentTimes.size()) * 100 == 100;
    }
}
