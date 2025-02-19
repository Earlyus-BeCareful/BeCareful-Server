package com.becareful.becarefulserver.domain.caregiver.dto.response;

import static java.time.format.DateTimeFormatter.ofPattern;

import com.becareful.becarefulserver.domain.common.domain.CareType;
import com.becareful.becarefulserver.domain.common.vo.Gender;
import com.becareful.becarefulserver.domain.recruitment.domain.CompletedMatching;

import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;

@Builder(access = AccessLevel.PRIVATE)
public record WorkScheduleResponse(
        String workStartTime,
        String workEndTime,
        String seniorName,
        Gender seniorGender,
        Integer seniorAge,
        List<CareType> seniorCareType,
        String workLocation
) {

    public static WorkScheduleResponse from(CompletedMatching completedMatching) {
        return WorkScheduleResponse.builder()
                .workStartTime(completedMatching.getContract().getWorkStartTime().format(ofPattern("HH:mm")))
                .workEndTime(completedMatching.getContract().getWorkEndTime().format(ofPattern("HH:mm")))
                .seniorName(completedMatching.getContract().getMatching().getRecruitment().getElderly().getName())
                .seniorGender(completedMatching.getContract().getMatching().getRecruitment().getElderly().getGender())
                .seniorAge(completedMatching.getContract().getMatching().getRecruitment().getElderly().getAge())
                .seniorCareType(completedMatching.getContract().getCareTypes().stream().toList())
                .workLocation(completedMatching.getContract().getMatching().getRecruitment().getElderly().getResidentialAddress().getFullAddress())
                .build();
    }
}
