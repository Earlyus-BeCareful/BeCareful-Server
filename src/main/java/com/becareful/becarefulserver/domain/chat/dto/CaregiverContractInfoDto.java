package com.becareful.becarefulserver.domain.chat.dto;

import com.becareful.becarefulserver.domain.caregiver.domain.Caregiver;

public record CaregiverContractInfoDto(String name, Integer age, String phoneNumber) {
    public static CaregiverContractInfoDto from(Caregiver caregiver) {
        return new CaregiverContractInfoDto(caregiver.getName(), caregiver.getAge(), caregiver.getPhoneNumber());
    }
}
