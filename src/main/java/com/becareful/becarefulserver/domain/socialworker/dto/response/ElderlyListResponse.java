package com.becareful.becarefulserver.domain.socialworker.dto.response;

import com.becareful.becarefulserver.domain.common.vo.Gender;
import com.becareful.becarefulserver.domain.socialworker.domain.vo.CareLevel;

public record ElderlyListResponse(
        Long elderlyId,
        String name,
        int age,
        Gender gender,
        String profileImageUrl,
        CareLevel careLevel,
        int caregiverNum,
        boolean isMatching) {}
