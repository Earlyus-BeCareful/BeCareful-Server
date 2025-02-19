package com.becareful.becarefulserver.domain.socialworker.dto.response;

import com.becareful.becarefulserver.domain.common.vo.Gender;
import com.becareful.becarefulserver.domain.socialworker.domain.Elderly;

public record SimpleElderlyResponse(
        String name,
        Integer age,
        Gender gender,
        String profileImageUrl
) {

    public static SimpleElderlyResponse from(Elderly elderly) {
        return new SimpleElderlyResponse(
                elderly.getName(),
                elderly.getAge(),
                elderly.getGender(),
                elderly.getProfileImageUrl()
        );
    }
}
