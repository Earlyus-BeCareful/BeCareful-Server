package com.becareful.becarefulserver.domain.matching.dto;

import com.becareful.becarefulserver.domain.caregiver.domain.WorkSalaryType;
import com.becareful.becarefulserver.domain.common.domain.CareType;
import com.becareful.becarefulserver.domain.matching.domain.Recruitment;
import java.time.DayOfWeek;
import java.time.format.DateTimeFormatter;
import java.util.List;

public record RecruitmentDto(
        Long recruitmentId,
        String title,
        List<CareType> careTypes,
        List<DayOfWeek> workDays,
        String workStartTime,
        String workEndTime,
        WorkSalaryType workSalaryType,
        Integer workSalaryAmount,
        boolean isRecruiting,
        String institutionName) {

    public static RecruitmentDto from(Recruitment recruitment) {
        return new RecruitmentDto(
                recruitment.getId(),
                recruitment.getTitle(),
                recruitment.getCareTypes().stream().toList(),
                recruitment.getWorkDays().stream().toList(),
                recruitment.getWorkStartTime().format(DateTimeFormatter.ofPattern("HH:mm")),
                recruitment.getWorkEndTime().format(DateTimeFormatter.ofPattern("HH:mm")),
                recruitment.getWorkSalaryType(),
                recruitment.getWorkSalaryAmount(),
                recruitment.isRecruiting(),
                recruitment.getElderly().getNursingInstitution().getName());
    }
}
