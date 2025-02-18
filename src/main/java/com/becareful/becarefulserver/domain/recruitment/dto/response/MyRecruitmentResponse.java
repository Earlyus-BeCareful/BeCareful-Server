package com.becareful.becarefulserver.domain.recruitment.dto.response;

import com.becareful.becarefulserver.domain.recruitment.domain.MatchingStatus;
import com.becareful.becarefulserver.domain.recruitment.domain.Recruitment;

import java.time.format.DateTimeFormatter;

public record MyRecruitmentResponse(
    RecruitmentResponse recruitmentInfo,
    MatchingStatus matchingStatus
) {
    public static MyRecruitmentResponse of(Recruitment recruitment, MatchingStatus matchingStatus) {
        return new MyRecruitmentResponse(
                new RecruitmentResponse(
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
                        98, // TODO : 매칭율 계산 후 외부에서 주입
                        false,
                        false
                ),
                matchingStatus
        );
    }
}
