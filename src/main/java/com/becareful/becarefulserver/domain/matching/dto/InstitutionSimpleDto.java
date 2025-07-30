package com.becareful.becarefulserver.domain.matching.dto;

import com.becareful.becarefulserver.domain.nursing_institution.domain.NursingInstitution;

public record InstitutionSimpleDto(String name, String address) {
    public static InstitutionSimpleDto from(NursingInstitution institution) {
        return new InstitutionSimpleDto(
                institution.getName(), institution.getAddress().getFullAddress());
    }
}
