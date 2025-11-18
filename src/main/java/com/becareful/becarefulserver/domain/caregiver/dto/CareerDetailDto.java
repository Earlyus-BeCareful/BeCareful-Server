package com.becareful.becarefulserver.domain.caregiver.dto;

import com.becareful.becarefulserver.domain.caregiver.domain.CareerDetail;

public record CareerDetailDto(String workInstitution, String workYear) {

    public static CareerDetailDto from(CareerDetail careerDetail) {
        return new CareerDetailDto(careerDetail.getWorkInstitution(), careerDetail.getWorkYear());
    }
}
