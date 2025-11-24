package com.becareful.becarefulserver.domain.matching.dto;

import com.becareful.becarefulserver.domain.common.domain.Gender;
import com.becareful.becarefulserver.domain.socialworker.domain.Elderly;
import com.becareful.becarefulserver.domain.socialworker.domain.CareLevel;

public record ElderlySimpleDto(
        Long elderlyId,
        String elderlyName,
        Integer elderlyAge,
        Gender elderlyGender,
        String elderlyLocation,
        CareLevel elderlyCareLevel,
        String elderlyProfileImageUrl) {
    public static ElderlySimpleDto from(Elderly elderly) {
        return new ElderlySimpleDto(
                elderly.getId(),
                elderly.getName(),
                elderly.getAge(),
                elderly.getGender(),
                elderly.getResidentialLocation().getShortLocation(),
                elderly.getCareLevel(),
                elderly.getProfileImageUrl());
    }
}
