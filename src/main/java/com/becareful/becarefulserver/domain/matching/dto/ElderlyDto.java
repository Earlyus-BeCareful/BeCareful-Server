package com.becareful.becarefulserver.domain.matching.dto;

import com.becareful.becarefulserver.domain.common.domain.CareType;
import com.becareful.becarefulserver.domain.common.domain.DetailCareType;
import com.becareful.becarefulserver.domain.common.domain.Gender;
import com.becareful.becarefulserver.domain.socialworker.domain.Elderly;
import com.becareful.becarefulserver.domain.socialworker.domain.vo.CareLevel;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public record ElderlyDto(
        String name,
        Gender gender,
        LocalDate birthDate,
        Integer age,
        String address,
        String profileImageUrl,
        CareLevel careLevel,
        List<CareTypeDto> detailCareTypes,
        String healthCondition,
        String institutionName,
        boolean hasInmate,
        boolean hasPet) {

    public static ElderlyDto from(Elderly elderly) {
        Map<CareType, List<String>> groupedCareInfo = elderly.getDetailCareTypes().stream()
                .collect(Collectors.groupingBy(
                        DetailCareType::getCareType,
                        Collectors.mapping(DetailCareType::getDisplayName, Collectors.toList())));

        return new ElderlyDto(
                elderly.getName(),
                elderly.getGender(),
                elderly.getBirthday(),
                elderly.getAge(),
                elderly.getResidentialLocation().getFullLocation(),
                elderly.getProfileImageUrl(),
                elderly.getCareLevel(),
                groupedCareInfo.entrySet().stream()
                        .map(entry -> new CareTypeDto(entry.getKey(), entry.getValue()))
                        .toList(),
                elderly.getHealthCondition(),
                elderly.getNursingInstitution().getName(),
                elderly.isHasInmate(),
                elderly.isHasPet());
    }
}
