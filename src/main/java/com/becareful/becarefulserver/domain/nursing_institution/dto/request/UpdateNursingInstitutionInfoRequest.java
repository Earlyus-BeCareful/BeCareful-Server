package com.becareful.becarefulserver.domain.nursing_institution.dto.request;

import com.becareful.becarefulserver.domain.nursing_institution.vo.*;
import jakarta.validation.constraints.*;
import java.util.*;

public record UpdateNursingInstitutionInfoRequest(
        @NotBlank String institutionName,
        @NotBlank @Size(min = 1, max = 20) String institutionCode,
        @NotNull Integer openYear,
        @NotNull @Size(min = 1, max = 6) List<FacilityType> facilityTypeList,
        @NotBlank String phoneNumber,
        @NotBlank String profileImageUrl) {}
