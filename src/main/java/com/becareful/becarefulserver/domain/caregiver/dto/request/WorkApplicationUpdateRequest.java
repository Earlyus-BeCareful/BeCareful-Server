package com.becareful.becarefulserver.domain.caregiver.dto.request;

import com.becareful.becarefulserver.domain.caregiver.domain.WorkSalaryUnitType;
import com.becareful.becarefulserver.domain.caregiver.domain.WorkTime;
import com.becareful.becarefulserver.domain.common.domain.CareType;
import com.becareful.becarefulserver.domain.work_location.dto.request.WorkLocationDto;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.DayOfWeek;
import java.util.List;

public record WorkApplicationUpdateRequest(
        @Size(min = 1) List<WorkLocationDto> workLocations,
        @Size(min = 1) List<DayOfWeek> workDays,
        @Size(min = 1) List<WorkTime> workTimes,
        @Size(min = 1) List<CareType> careTypes,
        @NotNull WorkSalaryUnitType workSalaryUnitType,
        @NotNull Integer workSalaryAmount) {}
