package com.becareful.becarefulserver.domain.caregiver.dto;

import com.becareful.becarefulserver.domain.caregiver.domain.Career;
import java.time.format.DateTimeFormatter;

public record CareerSimpleDto(Long careerId, String careerTitle, String lastModifiedDate) {
    public static CareerSimpleDto from(Career career) {
        return new CareerSimpleDto(
                career.getId(),
                career.getTitle(),
                career.getUpdateDate().format(DateTimeFormatter.ofPattern("yyyy.MM.dd")));
    }
}
