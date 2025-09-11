package com.becareful.becarefulserver.domain.common.domain;

import static com.becareful.becarefulserver.global.exception.ErrorMessage.USER_CREATE_INVALID_GENDER_CODE;

import com.becareful.becarefulserver.global.exception.exception.SocialWorkerException;

public enum Gender {
    MALE,
    FEMALE;

    public static Gender fromGenderCode(int genderCode) {
        switch (genderCode) {
            case 1, 3 -> {
                return MALE;
            }
            case 2, 4 -> {
                return FEMALE;
            }
            default -> throw new SocialWorkerException(USER_CREATE_INVALID_GENDER_CODE);
        }
    }
}
