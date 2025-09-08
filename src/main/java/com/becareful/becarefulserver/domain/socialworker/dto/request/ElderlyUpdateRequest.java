package com.becareful.becarefulserver.domain.socialworker.dto.request;

import com.becareful.becarefulserver.domain.common.domain.DetailCareType;
import com.becareful.becarefulserver.domain.common.domain.Gender;
import com.becareful.becarefulserver.domain.socialworker.domain.vo.CareLevel;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public record ElderlyUpdateRequest(
        Optional<String> name,
        Optional<LocalDate> birthday,
        Optional<Boolean> inmate,
        Optional<Boolean> pet,
        Optional<Gender> gender,
        Optional<CareLevel> careLevel,
        Optional<String> siDo,
        Optional<String> siGuGun,
        Optional<String> eupMyeonDong,
        Optional<String> detailAddress,
        Optional<String> profileImageUrl,
        Optional<String> healthCondition,
        Optional<List<DetailCareType>> detailCareTypeList) {}
