package com.becareful.becarefulserver.domain.matching.dto.response;

import static java.time.format.DateTimeFormatter.ofPattern;
import static java.util.stream.Collectors.groupingBy;

import com.becareful.becarefulserver.domain.caregiver.domain.WorkSalaryType;
import com.becareful.becarefulserver.domain.common.domain.DetailCareType;
import com.becareful.becarefulserver.domain.matching.domain.Recruitment;
import com.becareful.becarefulserver.domain.matching.dto.response.ElderlyInfoResponse.CareInfoResponse;
import com.becareful.becarefulserver.domain.socialworker.domain.Elderly;

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
        String description,
        List<CareInfoResponse> careInfoList
) {

    public static RecruitmentInfoResponse from(Recruitment recruitment, Elderly elderly) {
        return new RecruitmentInfoResponse(
                recruitment.getId(),
                recruitment.getTitle(),
                recruitment.getWorkDays().stream().toList(),
                recruitment.getWorkStartTime().format(ofPattern("HH:mm")),
                recruitment.getWorkEndTime().format(ofPattern("HH:mm")),
                recruitment.getWorkSalaryType(),
                recruitment.getWorkSalaryAmount(),
                recruitment.getDescription(),
                elderly.getDetailCareTypes().stream()
                        .collect(groupingBy(DetailCareType::getCareType))
                        .entrySet().stream()
                        .filter(entry -> recruitment.getCareTypes().contains(entry.getKey()))
                        .map(entry -> new CareInfoResponse(
                                entry.getKey(),
                                entry.getValue().stream()
                                        .map(DetailCareType::getDisplayName)
                                        .toList()))
                        .toList()
        );
    }
}
