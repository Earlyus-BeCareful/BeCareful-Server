package com.becareful.becarefulserver.domain.recruitment.dto.request;

import com.becareful.becarefulserver.domain.caregiver.domain.WorkSalaryType;
import com.becareful.becarefulserver.domain.common.domain.CareType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
public record ContractEditRequest(

    @NotNull Long matchingId,

    @Size(min = 1) List<DayOfWeek> workDays,

    @NotNull LocalTime workStartTime,

    @NotNull LocalTime workEndTime,
    @NotNull WorkSalaryType workSalaryType,
    @NotNull int workSalaryAmount,
    @NotNull LocalDate workStartDate,

    @Size(min = 1) List<CareType> careTypes
)
{}
