package com.becareful.becarefulserver.domain.socialworker.dto.response;

import com.becareful.becarefulserver.domain.common.vo.Gender;
import com.becareful.becarefulserver.domain.socialworker.domain.Elderly;
import com.becareful.becarefulserver.domain.socialworker.domain.vo.CareLevel;

public record ElderlyInfoResponse(
        Long elderlyId,
        String name,
        int age,
        Gender gender,
        String profileImageUrl,
        CareLevel careLevel,
        int caregiverNum,
        boolean isMatching) {

    public static ElderlyInfoResponse of(Elderly elderly, int caregiverNum, boolean isMatching) {
        return new ElderlyInfoResponse(
                elderly.getId(),
                elderly.getName(),
                elderly.getAge(),
                elderly.getGender(),
                elderly.getProfileImageUrl(),
                elderly.getCareLevel(),
                caregiverNum, // 매칭 완료 테이블에서 어르신
                isMatching);
    }
}
