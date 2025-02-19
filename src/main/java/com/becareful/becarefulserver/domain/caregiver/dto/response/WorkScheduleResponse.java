package com.becareful.becarefulserver.domain.caregiver.dto.response;

import static java.time.format.DateTimeFormatter.ofPattern;

import com.becareful.becarefulserver.domain.common.domain.CareType;
import com.becareful.becarefulserver.domain.common.vo.Gender;
import com.becareful.becarefulserver.domain.recruitment.domain.CompletedMatching;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
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
    public static WorkScheduleResponse createDummy() {
        return WorkScheduleResponse.builder()
                .workStartTime(LocalTime.of(8, 20).format(ofPattern("HH:mm")))
                .workEndTime(LocalTime.of(17, 20).format(ofPattern("HH:mm")))
                .seniorName("박순자")
                .seniorGender(Gender.FEMALE)
                .seniorAge(65)
                .seniorCareType(List.of(CareType.식사보조, CareType.이동보조))
                .workLocation("노원구 상계동")
                .build();
    }

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
