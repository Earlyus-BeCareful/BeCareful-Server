package com.becareful.becarefulserver.domain.socialworker.dto.request;

import com.becareful.becarefulserver.domain.common.domain.DetailCareType;
import com.becareful.becarefulserver.domain.common.domain.Gender;
import com.becareful.becarefulserver.domain.socialworker.domain.vo.CareLevel;
import java.time.LocalDate;
import java.util.List;

public record ElderlyUpdateRequest(
        String name,
        LocalDate birthday,
        Boolean inmate,
        Boolean pet,
        Gender gender,
        CareLevel careLevel,
        String siDo, // TODO : Address VO 로 변경
        String siGuGun,
        String eupMyeonDong,
        String detailAddress,
        String profileImageTempKey,
        String healthCondition,
        List<DetailCareType> detailCareTypeList) {}
