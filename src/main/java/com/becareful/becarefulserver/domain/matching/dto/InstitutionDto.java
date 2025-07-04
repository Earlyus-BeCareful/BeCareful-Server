package com.becareful.becarefulserver.domain.matching.dto;

import com.becareful.becarefulserver.domain.nursing_institution.domain.NursingInstitution;

public record InstitutionDto(String name, String address) {
    public static InstitutionDto from(NursingInstitution institution) {
        return new InstitutionDto(
                institution.getName(), institution.getAddress().getFullAddress());
    }
}
