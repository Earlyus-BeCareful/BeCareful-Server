package com.becareful.becarefulserver.domain.socialworker.dto;

import com.becareful.becarefulserver.domain.nursing_institution.domain.NursingInstitution;
import com.becareful.becarefulserver.domain.nursing_institution.domain.vo.FacilityType;
import java.time.LocalDate;
import java.util.EnumSet;

public record InstitutionDto(
        String institutionName,
        String institutionImageUrl,
        LocalDate institutionLastUpdate,
        int institutionOpenYear,
        EnumSet<FacilityType> facilityTypes,
        String institutionPhoneNumber) {

    public static InstitutionDto from(NursingInstitution institution) {
        return new InstitutionDto(
                institution.getName(),
                institution.getProfileImageUrl(),
                institution.getUpdateDate().toLocalDate(),
                institution.getOpenYear(),
                institution.getFacilityTypes(),
                institution.getInstitutionPhoneNumber());
    }
}
