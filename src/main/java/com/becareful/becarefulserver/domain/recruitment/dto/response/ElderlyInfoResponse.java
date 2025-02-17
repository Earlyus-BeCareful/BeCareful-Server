package com.becareful.becarefulserver.domain.recruitment.dto.response;

import com.becareful.becarefulserver.domain.common.domain.CareType;
import com.becareful.becarefulserver.domain.common.vo.Gender;
import com.becareful.becarefulserver.domain.socialworker.domain.Elderly;
import com.becareful.becarefulserver.domain.socialworker.domain.vo.CareLevel;

import java.util.List;

public record ElderlyInfoResponse(
        String name,
        String address,
        Gender gender,
        Integer age,
        boolean hasInmate,
        boolean hasPet,
        String profileImageUrl,
        CareLevel careLevel,
        String healthCondition
) {
    public record CareInfoResponse(
            CareType careType,
            List<String> detailCareTypes
    ) {}

    public static ElderlyInfoResponse from(Elderly elderly) {
        return new ElderlyInfoResponse(
                elderly.getName(),
                elderly.getResidentialAddress().getFullAddress(),
                elderly.getGender(),
                elderly.getAge(),
                elderly.isInmate(),
                elderly.isPet(),
                elderly.getProfileImageUrl(),
                elderly.getCareLevel(),
                elderly.getHealthCondition()
        );
    }
}
