package com.becareful.becarefulserver.domain.caregiver.dto;

import com.becareful.becarefulserver.domain.caregiver.domain.WorkApplication;
import com.becareful.becarefulserver.domain.caregiver.domain.WorkTime;
import com.becareful.becarefulserver.domain.common.domain.CareType;
import java.time.DayOfWeek;
import java.util.List;

public record WorkApplicationDto(
        Long workApplicationId,
        List<DayOfWeek> workDays,
        List<WorkTime> workTimes,
        Integer workSalaryAmount,
        List<CareType> careTypes,
        List<String> certificates,
        boolean isHavingCar,
        boolean isCompleteDementiaEducation) {
    public static WorkApplicationDto from(WorkApplication workApplication) {
        return new WorkApplicationDto(
                workApplication.getId(),
                workApplication.getWorkDays().stream().toList(),
                workApplication.getWorkTimes().stream().toList(),
                workApplication.getWorkSalaryAmount(),
                workApplication.getWorkCareTypes().stream().toList(),
                workApplication.getCaregiver().getCertificateNames(),
                workApplication.getCaregiver().getCaregiverInfo().isHavingCar(),
                workApplication.getCaregiver().getCaregiverInfo().isCompleteDementiaEducation());
    }
}
