package com.becareful.becarefulserver.domain.matching.dto.response;

import com.becareful.becarefulserver.domain.common.domain.CareType;
import com.becareful.becarefulserver.domain.common.vo.Gender;
import com.becareful.becarefulserver.domain.matching.domain.CompletedMatching;
import com.becareful.becarefulserver.domain.socialworker.domain.Elderly;

import java.time.DayOfWeek;
import java.util.List;

public record CompletedMatchingInfoResponse(
        Long id,
        String elderlyName,
        Integer elderlyAge,
        Gender elderlyGender,
        String elderlyProfileImageUrl,
        List<DayOfWeek> workDays,
        String workAddress,
        List<CareType> careTypes,
        String healthCondition,
        String institutionName,
        String note
) {
    public static CompletedMatchingInfoResponse from(CompletedMatching completedMatching){
        Elderly elderly = completedMatching.getContract().getMatching().getRecruitment().getElderly();

        return new CompletedMatchingInfoResponse(
            completedMatching.getId(),
            elderly.getName(),
            elderly.getAge(),
            elderly.getGender(),
            elderly.getProfileImageUrl(),
            completedMatching.getContract().getWorkDays().stream().toList(),
            elderly.getResidentialAddress().getFullAddress(),
            completedMatching.getContract().getCareTypes().stream().toList(),
            elderly.getHealthCondition(),
            elderly.getNursingInstitution().getName(),
            completedMatching.getNote()
        );
    }
}
