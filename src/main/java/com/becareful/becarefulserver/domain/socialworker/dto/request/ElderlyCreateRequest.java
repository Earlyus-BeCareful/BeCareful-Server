package com.becareful.becarefulserver.domain.socialworker.dto.request;

import com.becareful.becarefulserver.domain.common.domain.DetailCareType;
import com.becareful.becarefulserver.domain.common.vo.Gender;
import com.becareful.becarefulserver.domain.socialworker.domain.NursingInstitution;
import com.becareful.becarefulserver.domain.socialworker.domain.vo.CareLevel;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.List;

public record ElderlyCreateRequest (
    @NotBlank String name,
    @NotNull LocalDate birthday,
    @NotNull boolean inmate,
    @NotNull boolean pet,
    @NotBlank String institutionId,
    @NotNull Gender gender,
    @NotNull CareLevel careLevel,
    @NotBlank String siDo,
    @NotBlank String siGuGun,
    @NotBlank String eupMyeonDong,
    String detailAddress,
    String profileImageUrl,
    @NotBlank String healthCondition,
    @Size(min = 1) List<DetailCareType> detailCareTypeList
)
{}
