package com.becareful.becarefulserver.domain.matching.dto;

import com.becareful.becarefulserver.domain.caregiver.domain.Career;
import com.becareful.becarefulserver.domain.caregiver.domain.Caregiver;

public record CaregiverSimpleDto(Long caregiverId, String profileImageUrl, String name, String applicationTitle) {
    public static CaregiverSimpleDto of(Caregiver caregiver, Career career) {
        return new CaregiverSimpleDto(
                caregiver.getId(), caregiver.getProfileImageUrl(), caregiver.getName(), career.getTitle());
    }
}
