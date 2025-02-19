package com.becareful.becarefulserver.domain.recruitment.dto.response;

import com.becareful.becarefulserver.domain.common.domain.CareType;
import com.becareful.becarefulserver.domain.common.vo.Gender;
import com.becareful.becarefulserver.domain.recruitment.domain.CompletedMatching;
import com.becareful.becarefulserver.domain.socialworker.domain.Elderly;

import java.util.EnumSet;

public record CompletedMatchingInfoResponse(
        Long id,
        String elderlyName,
        Integer elderlyAge,
        Gender elderlyGender,
        String elderlyProfileImageUrl,
        String workDays,
        String workAddress,
        EnumSet<CareType>careTypes,
        String healthCondition,
        String institutionName,
        String note
) {
    public static CompletedMatchingInfoResponse form(CompletedMatching completedMatching, Elderly elderly){
        return new CompletedMatchingInfoResponse(
        completedMatching.getId(),
        elderly.getName(),
        elderly.getAge(),
        elderly.getGender(),
        elderly.getProfileImageUrl(),
        completedMatching.getWorkDays(),
        completedMatching.getWorkAddress(),
        completedMatching.getWorkCareTypes(),
        elderly.getHealthCondition(),
        completedMatching.getInstitutionName(),
        completedMatching.getNote()
        );
    }
}
