package com.becareful.becarefulserver.domain.recruitment.domain;

import com.becareful.becarefulserver.domain.caregiver.domain.WorkSalaryType;
import com.becareful.becarefulserver.domain.caregiver.domain.converter.CareTypeSetConverter;
import com.becareful.becarefulserver.domain.caregiver.domain.converter.DayOfWeekSetConverter;
import com.becareful.becarefulserver.domain.common.domain.BaseEntity;
import com.becareful.becarefulserver.domain.common.domain.CareType;
import com.becareful.becarefulserver.domain.recruitment.dto.response.ElderlyInfoResponse;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.EnumSet;
import java.util.List;

@Entity
public class Contract extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @JoinColumn(name = "matching_id")
    @ManyToOne(fetch = FetchType.LAZY )
    private Matching matching;

    @Convert(converter = DayOfWeekSetConverter.class)
    public EnumSet<DayOfWeek> workDays;

    public LocalTime workStartTime;
    public LocalTime workEndTime;
    private WorkSalaryType workSalaryType;

    public int workSalaryAmount;
    public LocalDate workStartDate;

    @Convert(converter = CareTypeSetConverter.class)
    private EnumSet<CareType> careTypes;

    @Builder(access = AccessLevel.PRIVATE)
    private Contract(Matching matching,
                     EnumSet<DayOfWeek> workDays, LocalTime workStartTime, LocalTime workEndTime, LocalDate workStartDate,
                     WorkSalaryType workSalaryType, int workSalaryAmount, EnumSet<CareType> careTypes){
        this.matching = matching;
        this.workDays = workDays;
        this.workStartTime = workStartTime;
        this.workEndTime = workEndTime;
        this.workStartDate = workStartDate;
        this.workSalaryType = workSalaryType;
        this.workSalaryAmount = workSalaryAmount;
        this.careTypes = careTypes;
    }

    public static Contract create(Matching matching, Recruitment recruitment, LocalDate workStartDate){
        return Contract.builder()
                .matching(matching)
                .workDays(recruitment.getWorkDays())
                .workStartTime(recruitment.getWorkStartTime())
                .workEndTime(recruitment.getWorkEndTime())
                .workStartDate(workStartDate)
                .workSalaryType(recruitment.getWorkSalaryType())
                .workSalaryAmount(recruitment.getWorkSalaryAmount())
                .careTypes(recruitment.getCareTypes())
                .build();
    }
}
