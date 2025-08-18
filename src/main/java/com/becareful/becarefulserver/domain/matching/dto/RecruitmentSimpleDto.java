package com.becareful.becarefulserver.domain.matching.dto;

import com.becareful.becarefulserver.domain.caregiver.domain.WorkSalaryUnitType;
import com.becareful.becarefulserver.domain.common.domain.CareType;
import com.becareful.becarefulserver.domain.matching.domain.Recruitment;
import java.time.DayOfWeek;
import java.time.format.DateTimeFormatter;
import java.util.List;

public record RecruitmentSimpleDto(
        Long recruitmentId,
        String title,
        List<CareType> careTypes,
        List<DayOfWeek> workDays,
        String workStartTime,
        String workEndTime,
        WorkSalaryUnitType workSalaryUnitType,
        Integer workSalaryAmount,
        boolean isRecruiting,
        InstitutionSimpleDto institutionInfo) {

    public static RecruitmentSimpleDto from(Recruitment recruitment) {
        return new RecruitmentSimpleDto(
                recruitment.getId(),
                recruitment.getTitle(),
                recruitment.getCareTypes().stream().toList(),
                recruitment.getWorkDays().stream().toList(),
                recruitment.getWorkStartTime().format(DateTimeFormatter.ofPattern("HH:mm")),
                recruitment.getWorkEndTime().format(DateTimeFormatter.ofPattern("HH:mm")),
                recruitment.getWorkSalaryUnitType(),
                recruitment.getWorkSalaryAmount(),
                recruitment.isRecruiting(),
                InstitutionSimpleDto.from(recruitment.getElderly().getNursingInstitution()));
    }
}
