package com.becareful.becarefulserver.domain.caregiver.dto.response;

import com.becareful.becarefulserver.domain.caregiver.domain.CareerDetail;

public record CareerDetailResponse(
        String workInstitution,
        String workYear
) {

    public static CareerDetailResponse from(CareerDetail careerDetail) {
        return new CareerDetailResponse(
                careerDetail.getWorkInstitution(),
                careerDetail.getWorkYear());
    }
}
