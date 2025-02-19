package com.becareful.becarefulserver.domain.recruitment.dto.response;

import com.becareful.becarefulserver.domain.caregiver.domain.WorkSalaryType;
import com.becareful.becarefulserver.domain.common.domain.CareType;
import com.becareful.becarefulserver.domain.recruitment.domain.Matching;
import com.becareful.becarefulserver.domain.recruitment.domain.Recruitment;
import com.becareful.becarefulserver.domain.recruitment.domain.vo.MatchingInfo;

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

    public static RecruitmentResponse from(Matching matching) {
        Recruitment recruitment = matching.getRecruitment();
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
                calculateAverageMatchingRate(matching.getCaregiverMatchingInfo()).intValue(),
                false,
                false
        );
    }

    private static Double calculateAverageMatchingRate(MatchingInfo caregiverMatchingInfo) {
        return (caregiverMatchingInfo.getWorkCareTypeMatchingRate()
                + caregiverMatchingInfo.getWorkSalaryMatchingRate()
                + caregiverMatchingInfo.getWorkTimeMatchingRate()
                + caregiverMatchingInfo.getWorkDayMatchingRate()
                + caregiverMatchingInfo.getWorkLocationMatchingRate()) / 5;
    }
}
