package com.becareful.becarefulserver.domain.matching.dto;

import com.becareful.becarefulserver.domain.common.vo.Gender;
import com.becareful.becarefulserver.domain.socialworker.domain.Elderly;

public record ElderlySimpleDto(
        String elderlyName, Integer elderlyAge, Gender elderlyGender, String elderlyProfileImageUrl) {
    public static ElderlySimpleDto from(Elderly elderly) {
        return new ElderlySimpleDto(
                elderly.getName(), elderly.getAge(), elderly.getGender(), elderly.getProfileImageUrl());
    }
}
