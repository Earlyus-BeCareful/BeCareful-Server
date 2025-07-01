package com.becareful.becarefulserver.domain.matching.domain;

import com.becareful.becarefulserver.domain.caregiver.domain.WorkSalaryUnitType;
import com.becareful.becarefulserver.domain.caregiver.domain.WorkTime;
import com.becareful.becarefulserver.domain.caregiver.domain.converter.CareTypeSetConverter;
import com.becareful.becarefulserver.domain.caregiver.domain.converter.DayOfWeekSetConverter;
import com.becareful.becarefulserver.domain.common.domain.BaseEntity;
import com.becareful.becarefulserver.domain.common.domain.CareType;
import com.becareful.becarefulserver.domain.common.vo.Location;
import com.becareful.becarefulserver.domain.matching.dto.request.RecruitmentCreateRequest;
import com.becareful.becarefulserver.domain.socialworker.domain.Elderly;
import jakarta.persistence.*;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.EnumSet;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Recruitment extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "recruitment_id")
    private Long id;

    private String title;

    @Convert(converter = DayOfWeekSetConverter.class)
    private EnumSet<DayOfWeek> workDays;

    private LocalTime workStartTime;

    private LocalTime workEndTime;

    @Convert(converter = CareTypeSetConverter.class)
    private EnumSet<CareType> careTypes;

    @Enumerated(EnumType.STRING)
    private WorkSalaryUnitType workSalaryUnitType;

    private int workSalaryAmount;

    private String description;

    private boolean isRecruiting = true;

    @JoinColumn(name = "elderly_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private Elderly elderly;

    @Builder(access = AccessLevel.PRIVATE)
    private Recruitment(
            String title,
            EnumSet<DayOfWeek> workDays,
            LocalTime workStartTime,
            LocalTime workEndTime,
            EnumSet<CareType> careTypes,
            WorkSalaryUnitType workSalaryUnitType,
            int workSalaryAmount,
            String description,
            boolean isRecruiting,
            Elderly elderly) {
        this.title = title;
        this.workDays = workDays;
        this.workStartTime = workStartTime;
        this.workEndTime = workEndTime;
        this.careTypes = careTypes;
        this.workSalaryUnitType = workSalaryUnitType;
        this.workSalaryAmount = workSalaryAmount;
        this.description = description;
        this.isRecruiting = isRecruiting;
        this.elderly = elderly;
    }

    public static Recruitment create(RecruitmentCreateRequest request, Elderly elderly) {
        return Recruitment.builder()
                .title(request.title())
                .workDays(EnumSet.copyOf(request.workDays()))
                .workStartTime(request.workStartTime())
                .workEndTime(request.workEndTime())
                .careTypes(EnumSet.copyOf(request.careTypes()))
                .workSalaryType(request.workSalaryUnitType())
                .workSalaryAmount(request.workSalaryAmount())
                .description(request.description())
                .isRecruiting(true)
                .elderly(elderly)
                .build();
    }

    public EnumSet<WorkTime> getWorkTimes() {
        EnumSet<WorkTime> times = EnumSet.noneOf(WorkTime.class);

        if (isWorkTimeOverlap(LocalTime.of(8, 0), LocalTime.of(12, 0))) {
            times.add(WorkTime.MORNING);
        }

        if (isWorkTimeOverlap(LocalTime.of(12, 1), LocalTime.of(18, 0))) {
            times.add(WorkTime.AFTERNOON);
        }

        if (isWorkTimeOverlap(LocalTime.of(18, 1), LocalTime.of(22, 0))) {
            times.add(WorkTime.EVENING);
        }

        return times;
    }

    public Location getResidentialLocation() {
        return elderly.getResidentialLocation();
    }

    private boolean isWorkTimeOverlap(LocalTime startTime, LocalTime endTime) {
        return (workStartTime.isBefore(endTime) && startTime.isAfter(workEndTime))
                || (startTime.isBefore(workEndTime) && workStartTime.isBefore(endTime));
    }

    public void complete() {
        this.isRecruiting = false;
    }
}
