package com.becareful.becarefulserver.domain.nursing_institution.dto;

import com.becareful.becarefulserver.domain.nursing_institution.domain.NursingInstitution;

public record InstitutionSimpleDto(Long institutionId, String name, String address) {
    public static InstitutionSimpleDto from(NursingInstitution institution) {
        return new InstitutionSimpleDto(
                institution.getId(),
                institution.getName(),
                institution.getAddress().getFullAddress());
    }
}
