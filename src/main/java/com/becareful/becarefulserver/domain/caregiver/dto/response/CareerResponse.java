package com.becareful.becarefulserver.domain.caregiver.dto.response;

import com.becareful.becarefulserver.domain.caregiver.domain.Career;
import com.becareful.becarefulserver.domain.caregiver.domain.CareerType;
import java.util.List;

public record CareerResponse(
        String title, CareerType careerType, String introduce, List<CareerDetailResponse> careerDetails) {
    public static CareerResponse of(Career career, List<CareerDetailResponse> careerDetails) {
        return new CareerResponse(career.getTitle(), career.getCareerType(), career.getIntroduce(), careerDetails);
    }

    public static CareerResponse empty() {
        return new CareerResponse(null, null, null, List.of());
    }
}
