package com.becareful.becarefulserver.domain.matching.dto;

import com.becareful.becarefulserver.domain.caregiver.domain.Caregiver;

public record CaregiverSimpleDto(Long caregiverId, String name, String profileImageUrl) {
    public static CaregiverSimpleDto from(Caregiver caregiver) {
        return new CaregiverSimpleDto(caregiver.getId(), caregiver.getName(), caregiver.getProfileImageUrl());
    }
}
