package com.becareful.becarefulserver.domain.recruitment.dto.response;

import static java.util.stream.Collectors.groupingBy;

import com.becareful.becarefulserver.domain.common.domain.CareType;
import com.becareful.becarefulserver.domain.common.domain.DetailCareType;
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
        String healthCondition,
        List<CareInfoResponse> careInfoList
) {
    public record CareInfoResponse(
            CareType careType,
            List<String> detailCareTypes
    ) {}

    public static ElderlyInfoResponse from(Elderly elderly) {
        return new ElderlyInfoResponse(
                elderly.getName(),
                elderly.getAddress().getFullAddress(),
                elderly.getGender(),
                elderly.getAge(),
                elderly.getInmate(),
                elderly.getPet(),
                elderly.getProfileImageUrl(),
                elderly.getCareLevel(),
                elderly.getHealthCondition(),
                elderly.getDetailCareTypes().stream()
                        .collect(groupingBy(DetailCareType::getCareType))
                        .entrySet().stream()
                        .map(entry -> new CareInfoResponse(
                                    entry.getKey(),
                                    entry.getValue().stream()
                                            .map(DetailCareType::getDisplayName)
                                            .toList()))
                        .toList()
        );
    }
}
