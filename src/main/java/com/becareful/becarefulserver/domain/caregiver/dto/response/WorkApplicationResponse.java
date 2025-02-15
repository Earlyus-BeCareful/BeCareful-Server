package com.becareful.becarefulserver.domain.caregiver.dto.response;

import com.becareful.becarefulserver.domain.caregiver.domain.CareType;
import com.becareful.becarefulserver.domain.caregiver.domain.WorkApplication;
import com.becareful.becarefulserver.domain.caregiver.domain.WorkSalaryType;
import com.becareful.becarefulserver.domain.caregiver.domain.WorkTime;
import com.becareful.becarefulserver.domain.work_location.dto.request.WorkLocationDto;

import java.time.DayOfWeek;
import java.util.List;

public record WorkApplicationResponse(
        List<WorkLocationDto> workLocations,
        List<DayOfWeek> workDays,
        List<WorkTime> workTimes,
        List<CareType> careTypes,
        WorkSalaryType workSalaryType,
        Integer workSalaryAmount
) {
    public static WorkApplicationResponse of(List<WorkLocationDto> locations, WorkApplication application) {
        return new WorkApplicationResponse(
                locations,
                application.getWorkDays().stream().toList(),
                application.getWorkTimes().stream().toList(),
                application.getWorkCareTypes().stream().toList(),
                application.getWorkSalaryType(),
                application.getWorkSalaryAmount()
        );
    }

    public static WorkApplicationResponse empty() {
        return new WorkApplicationResponse(
                List.of(),
                List.of(),
                List.of(),
                List.of(),
                null,
                null
        );
    }
}
