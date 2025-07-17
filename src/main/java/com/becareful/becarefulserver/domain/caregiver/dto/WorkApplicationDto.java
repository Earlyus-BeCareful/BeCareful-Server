package com.becareful.becarefulserver.domain.caregiver.dto;

import com.becareful.becarefulserver.domain.caregiver.domain.WorkApplication;
import com.becareful.becarefulserver.domain.caregiver.domain.WorkSalaryUnitType;
import com.becareful.becarefulserver.domain.caregiver.domain.WorkTime;
import com.becareful.becarefulserver.domain.common.domain.CareType;
import com.becareful.becarefulserver.domain.work_location.dto.request.WorkLocationDto;
import java.time.DayOfWeek;
import java.time.format.DateTimeFormatter;
import java.util.List;

public record WorkApplicationDto(
        Long workApplicationId,
        boolean isActive,
        List<CareType> careTypes,
        List<DayOfWeek> workDays,
        List<WorkTime> workTimes,
        Integer workSalaryAmount,
        WorkSalaryUnitType workSalaryUnitType,
        String lastModifiedDate,
        List<WorkLocationDto> workLocations) {
    public static WorkApplicationDto of(List<WorkLocationDto> locations, WorkApplication application) {
        return new WorkApplicationDto(
                application.getId(),
                application.isActive(),
                application.getWorkCareTypes().stream().toList(),
                application.getWorkDays().stream().toList(),
                application.getWorkTimes().stream().toList(),
                application.getWorkSalaryAmount(),
                application.getWorkSalaryUnitType(),
                application.getUpdateDate().format(DateTimeFormatter.ofPattern("yyyy.MM.dd")),
                locations);
    }
}
