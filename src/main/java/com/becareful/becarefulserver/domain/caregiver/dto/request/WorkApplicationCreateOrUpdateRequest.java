package com.becareful.becarefulserver.domain.caregiver.dto.request;

import com.becareful.becarefulserver.domain.caregiver.domain.WorkSalaryUnitType;
import com.becareful.becarefulserver.domain.caregiver.domain.WorkTime;
import com.becareful.becarefulserver.domain.common.domain.CareType;
import com.becareful.becarefulserver.domain.common.domain.vo.Location;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.DayOfWeek;
import java.util.List;

public record WorkApplicationCreateOrUpdateRequest(
        @Size(min = 1) List<Location> workLocations,
        @Size(min = 1) List<DayOfWeek> workDays,
        @Size(min = 1) List<WorkTime> workTimes,
        @Size(min = 1) List<CareType> careTypes,
        @NotNull WorkSalaryUnitType workSalaryUnitType,
        @NotNull Integer workSalaryAmount) {}
