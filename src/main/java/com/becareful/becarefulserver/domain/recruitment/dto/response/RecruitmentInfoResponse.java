package com.becareful.becarefulserver.domain.recruitment.dto.response;

import static java.time.format.DateTimeFormatter.ofPattern;

import com.becareful.becarefulserver.domain.caregiver.domain.WorkSalaryType;
import com.becareful.becarefulserver.domain.recruitment.domain.Recruitment;

import java.time.DayOfWeek;
import java.util.List;

public record RecruitmentInfoResponse(
        Long recruitmentId,
        String title,
        List<DayOfWeek> workDays,
        String workStartTime,
        String workEndTime,
        WorkSalaryType workSalaryType,
        Integer workSalaryAmount,
        String description
) {

    public static RecruitmentInfoResponse from(Recruitment recruitment) {
        return new RecruitmentInfoResponse(
                recruitment.getId(),
                recruitment.getTitle(),
                recruitment.getWorkDays().stream().toList(),
                recruitment.getWorkStartTime().format(ofPattern("HH:mm")),
                recruitment.getWorkEndTime().format(ofPattern("HH:mm")),
                recruitment.getWorkSalaryType(),
                recruitment.getWorkSalaryAmount(),
                recruitment.getDescription()
        );
    }
}
