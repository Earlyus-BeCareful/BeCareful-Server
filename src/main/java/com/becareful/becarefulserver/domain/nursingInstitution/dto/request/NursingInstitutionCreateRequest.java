package com.becareful.becarefulserver.domain.nursingInstitution.dto.request;

import com.becareful.becarefulserver.domain.nursingInstitution.vo.FacilityType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

public record NursingInstitutionCreateRequest (
    @NotBlank String institutionName,
    @NotBlank @Size(min = 1, max = 20) String institutionCode,
    Integer openYear,
    @NotNull @Size(min = 1, max = 6)
    List<FacilityType>  facilityTypeList,
    @NotBlank String phoneNumber,
    @NotBlank String streetAddress,
    String detailAddress,
    String profileImageUrl
){}
