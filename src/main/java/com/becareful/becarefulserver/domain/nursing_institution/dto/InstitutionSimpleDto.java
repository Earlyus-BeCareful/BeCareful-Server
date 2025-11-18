package com.becareful.becarefulserver.domain.nursing_institution.dto;

import com.becareful.becarefulserver.domain.nursing_institution.domain.NursingInstitution;

public record InstitutionSimpleDto(
        Long institutionId, String institutionCode, String name, String profileImageUrl, String address) {
    public static InstitutionSimpleDto from(NursingInstitution institution) {
        return new InstitutionSimpleDto(
                institution.getId(),
                institution.getCode(),
                institution.getName(),
                institution.getProfileImageUrl(),
                institution.getAddress().getFullAddress());
    }
}
