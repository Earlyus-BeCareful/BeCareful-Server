package com.becareful.becarefulserver.domain.chat.dto;

public record CaregiverContractInfoDto(
        String name,
        Integer age,
        String phoneNumber
) {
    public static CaregiverContractInfoDto of(String name,Integer age,String phoneNumber) {
        return new CaregiverContractInfoDto(name,age,phoneNumber);
    }
}
