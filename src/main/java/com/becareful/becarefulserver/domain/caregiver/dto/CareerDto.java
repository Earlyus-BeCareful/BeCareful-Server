package com.becareful.becarefulserver.domain.caregiver.dto;

import com.becareful.becarefulserver.domain.caregiver.domain.Career;
import com.becareful.becarefulserver.domain.caregiver.domain.CareerDetail;
import com.becareful.becarefulserver.global.exception.exception.DomainException;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

public record CareerDto(
        Long careerId, String careerTitle, String lastModifiedDate, List<CareerDetailDto> careerDetails) {
    public static CareerDto from(List<CareerDetail> careerDetails) {
        if (careerDetails == null || careerDetails.isEmpty()) {
            throw new DomainException("[CareerDto] career detail 이 없습니다.");
        }
        Career career = careerDetails.get(0).getCareer();
        return new CareerDto(
                career.getId(),
                career.getTitle(),
                career.getUpdateDate().format(DateTimeFormatter.ofPattern("yyyy.MM.dd")),
                careerDetails.stream().map(CareerDetailDto::from).collect(Collectors.toList()));
    }

    private record CareerDetailDto(String workInstitution, String workYear) {
        public static CareerDetailDto from(CareerDetail careerDetail) {
            return new CareerDetailDto(careerDetail.getWorkInstitution(), careerDetail.getWorkYear());
        }
    }
}
