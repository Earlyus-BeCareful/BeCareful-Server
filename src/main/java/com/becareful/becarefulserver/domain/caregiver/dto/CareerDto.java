package com.becareful.becarefulserver.domain.caregiver.dto;

import com.becareful.becarefulserver.domain.caregiver.domain.Career;
import com.becareful.becarefulserver.domain.caregiver.domain.CareerDetail;
import com.becareful.becarefulserver.domain.caregiver.domain.CareerType;
import java.time.format.DateTimeFormatter;
import java.util.List;

public record CareerDto(
        Long careerId,
        String title,
        CareerType careerType,
        String introduce,
        String lastModifiedDate,
        List<CareerDetailDto> careerDetails) {
    public static CareerDto of(Career career, List<CareerDetail> careerDetails) {
        return new CareerDto(
                career.getId(),
                career.getTitle(),
                career.getCareerType(),
                career.getIntroduce(),
                career.getUpdateDate().format(DateTimeFormatter.ofPattern("yyyy.MM.dd")),
                careerDetails.stream().map(CareerDetailDto::from).toList());
    }

    public static CareerDto empty() {
        return new CareerDto(null, null, null, null, null, List.of());
    }
}
