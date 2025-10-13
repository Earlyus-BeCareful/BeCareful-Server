package com.becareful.becarefulserver.domain.matching.dto;

import static java.util.stream.Collectors.groupingBy;

import com.becareful.becarefulserver.domain.caregiver.domain.WorkSalaryUnitType;
import com.becareful.becarefulserver.domain.common.domain.DetailCareType;
import com.becareful.becarefulserver.domain.matching.domain.Recruitment;
import com.becareful.becarefulserver.domain.matching.domain.RecruitmentStatus;
import com.becareful.becarefulserver.domain.nursing_institution.dto.InstitutionSimpleDto;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public record RecruitmentDto(
        Long recruitmentId,
        String title,
        RecruitmentStatus recruitmentStatus,
        List<DayOfWeek> workDays,
        String workStartTime,
        String workEndTime,
        WorkSalaryUnitType workSalaryUnitType,
        Integer workSalaryAmount,
        String description,
        LocalDateTime createdAt,
        ElderlyDto elderlyInfo,
        InstitutionSimpleDto institutionInfo) {

    public static RecruitmentDto from(Recruitment recruitment) {
        return new RecruitmentDto(
                recruitment.getId(),
                recruitment.getTitle(),
                recruitment.getRecruitmentStatus(),
                recruitment.getWorkDays().stream().toList(),
                recruitment.getWorkStartTime().format(DateTimeFormatter.ofPattern("HH:mm")),
                recruitment.getWorkEndTime().format(DateTimeFormatter.ofPattern("HH:mm")),
                recruitment.getWorkSalaryUnitType(),
                recruitment.getWorkSalaryAmount(),
                recruitment.getDescription(),
                recruitment.getCreateDate(),
                ElderlyDto.from(recruitment.getElderly()),
                InstitutionSimpleDto.from(recruitment.getElderly().getNursingInstitution()));
    }
}
