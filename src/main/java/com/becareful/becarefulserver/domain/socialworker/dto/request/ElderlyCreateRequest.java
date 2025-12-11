package com.becareful.becarefulserver.domain.socialworker.dto.request;

import com.becareful.becarefulserver.domain.common.domain.DetailCareType;
import com.becareful.becarefulserver.domain.common.domain.Gender;
import com.becareful.becarefulserver.domain.common.domain.vo.Location;
import com.becareful.becarefulserver.domain.socialworker.domain.CareLevel;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.util.List;

public record ElderlyCreateRequest(
        // TODO : 에러 메세지 구체화
        @NotBlank String profileImageTempKey,
        @NotBlank String name,
        @NotNull LocalDate birthday,
        @NotNull Gender gender,
        @NotNull CareLevel careLevel,
        @NotNull Location residentialLocation,
        String detailAddress,
        @NotBlank String healthCondition,
        @NotNull @Size(min = 1) List<DetailCareType> detailCareTypeList,
        @NotNull Boolean hasInmate,
        @NotNull Boolean hasPet) {}
