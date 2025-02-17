package com.becareful.becarefulserver.domain.recruitment.dto.response;

import com.becareful.becarefulserver.domain.socialworker.domain.NursingInstitution;

public record InstitutionInfoResponse(
        String name,
        String address
) {
    public static InstitutionInfoResponse from(NursingInstitution institution) {
        return new InstitutionInfoResponse(
                institution.getName(),
                institution.getAddress().getFullAddress()
        );
    }
}
