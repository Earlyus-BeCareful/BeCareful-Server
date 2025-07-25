package com.becareful.becarefulserver.domain.caregiver.dto;

import com.becareful.becarefulserver.domain.caregiver.domain.Caregiver;
import com.becareful.becarefulserver.domain.caregiver.domain.vo.CaregiverInfo;
import com.becareful.becarefulserver.domain.common.vo.Gender;
import java.util.List;

public record CaregiverDto(
        Long caregiverId,
        String name,
        String phoneNumber,
        Gender gender,
        Integer age,
        String profileImageUrl,
        CaregiverInfo caregiverDetailInfo,
        List<String> certificates) {
    public static CaregiverDto from(Caregiver caregiver) {
        return new CaregiverDto(
                caregiver.getId(),
                caregiver.getName(),
                caregiver.getPhoneNumber(),
                caregiver.getGender(),
                caregiver.getAge(),
                caregiver.getProfileImageUrl(),
                caregiver.getCaregiverInfo(),
                caregiver.getCertificateNames());
    }
}
