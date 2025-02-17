package com.becareful.becarefulserver.domain.recruitment.dto.response;

import com.becareful.becarefulserver.domain.caregiver.domain.WorkSalaryType;
import com.becareful.becarefulserver.domain.common.domain.CareType;
import com.becareful.becarefulserver.domain.recruitment.domain.Recruitment;

import java.time.DayOfWeek;
import java.time.format.DateTimeFormatter;
import java.util.List;

public record RecruitmentResponse(
        Long recruitmentId,
        String title,
        List<CareType> careTypes,
        List<DayOfWeek> workDays,
        String workStartTime,
        String workEndTime,
        WorkSalaryType workSalaryType,
        Integer workSalaryAmount,
        boolean isRecruiting,
        String institutionName,
        Integer matchRate,
        boolean isHotRecruitment,
        boolean isHourlySalaryTop
) {

    public static RecruitmentResponse from(Recruitment recruitment) {
        return new RecruitmentResponse(
                recruitment.getId(),
                recruitment.getTitle(),
                recruitment.getCareTypes().stream().toList(),
                recruitment.getWorkDays().stream().toList(),
                recruitment.getWorkStartTime().format(DateTimeFormatter.ofPattern("HH:mm")),
                recruitment.getWorkEndTime().format(DateTimeFormatter.ofPattern("HH:mm")),
                recruitment.getWorkSalaryType(),
                recruitment.getWorkSalaryAmount(),
                recruitment.isRecruiting(),
                recruitment.getElderly().getNursingInstitution().getName(),
                98, // TODO : 매칭율 계산
                false,
                false
        );
    }
}
