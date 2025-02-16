package com.becareful.becarefulserver.domain.caregiver.domain;

import com.becareful.becarefulserver.domain.common.domain.CareType;
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
import jakarta.persistence.OneToOne;

import com.becareful.becarefulserver.domain.caregiver.domain.converter.CareTypeSetConverter;
import com.becareful.becarefulserver.domain.caregiver.domain.converter.DayOfWeekSetConverter;
import com.becareful.becarefulserver.domain.caregiver.domain.converter.WorkTimeSetConverter;
import com.becareful.becarefulserver.domain.caregiver.dto.request.WorkApplicationUpdateRequest;

import java.time.DayOfWeek;
import java.util.EnumSet;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class WorkApplication {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "work_application_id")
    private Long id;

    @Convert(converter = DayOfWeekSetConverter.class)
    private EnumSet<DayOfWeek> workDays;

    @Convert(converter = WorkTimeSetConverter.class)
    private EnumSet<WorkTime> workTimes;

    @Convert(converter = CareTypeSetConverter.class)
    private EnumSet<CareType> workCareTypes;

    @Enumerated(EnumType.STRING)
    private WorkSalaryType workSalaryType;

    private int workSalaryAmount;

    private boolean isActive;

    @JoinColumn(name = "caregiver_id")
    @OneToOne(fetch = FetchType.LAZY)
    private Caregiver caregiver;

    @Builder(access = AccessLevel.PRIVATE)
    public WorkApplication(EnumSet<DayOfWeek> workDays, EnumSet<WorkTime> workTimes,
            EnumSet<CareType> workCareTypes, WorkSalaryType workSalaryType, int workSalaryAmount,
            boolean isActive, Caregiver caregiver) {
        this.workDays = workDays;
        this.workTimes = workTimes;
        this.workCareTypes = workCareTypes;
        this.workSalaryType = workSalaryType;
        this.workSalaryAmount = workSalaryAmount;
        this.isActive = isActive;
        this.caregiver = caregiver;
    }

    public static WorkApplication create(WorkApplicationUpdateRequest request, Caregiver caregiver) {
        System.out.println(request);
        return WorkApplication.builder()
                .workCareTypes(EnumSet.copyOf(request.careTypes()))
                .workDays(EnumSet.copyOf(request.workDays()))
                .workTimes(EnumSet.copyOf(request.workTimes()))
                .workSalaryType(request.workSalaryType())
                .workSalaryAmount(request.workSalaryAmount())
                .isActive(true)
                .caregiver(caregiver)
                .build();
    }

    public void updateWorkApplication(WorkApplicationUpdateRequest request) {
        this.workDays = EnumSet.copyOf(request.workDays());
        this.workTimes = EnumSet.copyOf(request.workTimes());
        this.workCareTypes = EnumSet.copyOf(request.careTypes());
        this.workSalaryType = request.workSalaryType();
        this.workSalaryAmount = request.workSalaryAmount();
    }

    /***
     * Entity Method
     */

    public void activate() {
        this.isActive = true;
    }

    public void inactivate() {
        this.isActive = false;
    }
}
