package com.becareful.becarefulserver.domain.matching.dto;

import com.becareful.becarefulserver.domain.common.domain.CareType;
import com.becareful.becarefulserver.domain.common.vo.Gender;
import com.becareful.becarefulserver.domain.socialworker.domain.Elderly;
import com.becareful.becarefulserver.domain.socialworker.domain.vo.CareLevel;
import java.util.List;

public record ElderlyDto(
        String name,
        String address,
        Gender gender,
        Integer age,
        boolean hasInmate,
        boolean hasPet,
        String profileImageUrl,
        CareLevel careLevel,
        String healthCondition) {

    public static ElderlyDto from(Elderly elderly) {
        return new ElderlyDto(
                elderly.getName(),
                elderly.getResidentialLocation().getFullAddress(),
                elderly.getGender(),
                elderly.getAge(),
                elderly.isHasInmate(),
                elderly.isHasPet(),
                elderly.getProfileImageUrl(),
                elderly.getCareLevel(),
                elderly.getHealthCondition());
    }
}
