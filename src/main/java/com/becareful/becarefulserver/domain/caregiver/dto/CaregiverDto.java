package com.becareful.becarefulserver.domain.caregiver.dto;

import com.becareful.becarefulserver.domain.caregiver.domain.Caregiver;
import com.becareful.becarefulserver.domain.caregiver.domain.vo.Address;
import com.becareful.becarefulserver.domain.caregiver.domain.vo.CaregiverInfo;
import com.becareful.becarefulserver.domain.common.domain.Gender;
import java.time.format.DateTimeFormatter;
import java.util.List;

public record CaregiverDto(
        Long caregiverId,
        String name,
        String phoneNumber,
        Gender gender,
        Integer age,
        String birthday,
        Address address,
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
                caregiver.getBirthDate().format(DateTimeFormatter.ofPattern("yyMMdd")),
                caregiver.getAddress(),
                caregiver.getProfileImageUrl(),
                caregiver.getCaregiverInfo(),
                caregiver.getCertificateNames());
    }
}
