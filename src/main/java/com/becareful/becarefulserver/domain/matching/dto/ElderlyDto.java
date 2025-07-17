package com.becareful.becarefulserver.domain.matching.dto;

import com.becareful.becarefulserver.domain.common.vo.Gender;
import com.becareful.becarefulserver.domain.socialworker.domain.Elderly;
import com.becareful.becarefulserver.domain.socialworker.domain.vo.CareLevel;

public record ElderlyDto(
        String name,
        Gender gender,
        Integer age,
        String address,
        String profileImageUrl,
        CareLevel careLevel,
        String healthCondition,
        String institutionName,
        boolean hasInmate,
        boolean hasPet) {

    public static ElderlyDto from(Elderly elderly) {
        return new ElderlyDto(
                elderly.getName(),
                elderly.getGender(),
                elderly.getAge(),
                elderly.getResidentialLocation().getFullAddress(),
                elderly.getProfileImageUrl(),
                elderly.getCareLevel(),
                elderly.getHealthCondition(),
                elderly.getNursingInstitution().getName(),
                elderly.isHasInmate(),
                elderly.isHasPet());
    }
}
