package com.becareful.becarefulserver.domain.matching.dto;

import com.becareful.becarefulserver.domain.caregiver.domain.WorkSalaryType;
import com.becareful.becarefulserver.domain.common.domain.CareType;
import com.becareful.becarefulserver.domain.common.domain.DetailCareType;
import com.becareful.becarefulserver.domain.matching.domain.Recruitment;
import java.time.DayOfWeek;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static java.util.stream.Collectors.groupingBy;

public record RecruitmentDto(
        Long recruitmentId,
        String title,
        List<CareTypeDto> careTypes,
        List<DayOfWeek> workDays,
        String workStartTime,
        String workEndTime,
        WorkSalaryType workSalaryType,
        Integer workSalaryAmount,
        String description,
        boolean isRecruiting,
        String institutionName) {

    public static RecruitmentDto from(Recruitment recruitment) {
        return new RecruitmentDto(
                recruitment.getId(),
                recruitment.getTitle(),
                recruitment.getElderly().getDetailCareTypes().stream()
                        .collect(groupingBy(DetailCareType::getCareType))
                        .entrySet()
                        .stream()
                        .filter(entry -> recruitment.getCareTypes().contains(entry.getKey()))
                        .map(entry -> new CareTypeDto(
                                entry.getKey(),
                                entry.getValue().stream()
                                        .map(DetailCareType::getDisplayName)
                                        .toList()))
                        .toList(),
                recruitment.getWorkDays().stream().toList(),
                recruitment.getWorkStartTime().format(DateTimeFormatter.ofPattern("HH:mm")),
                recruitment.getWorkEndTime().format(DateTimeFormatter.ofPattern("HH:mm")),
                recruitment.getWorkSalaryType(),
                recruitment.getWorkSalaryAmount(),
                recruitment.getDescription(),
                recruitment.isRecruiting(),
                recruitment.getElderly().getNursingInstitution().getName());
    }
}
