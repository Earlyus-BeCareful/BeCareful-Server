package com.becareful.becarefulserver.domain.recruitment.domain.vo;

import jakarta.persistence.Embeddable;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString
@Getter
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MatchingInfo {

    private Double workDayMatchingRate;
    private Double workTimeMatchingRate;
    private Double workLocationMatchingRate;
    private Double workCareTypeMatchingRate;
    private Double workSalaryMatchingRate;
    private Integer workSalaryDifference;

    @Builder
    public MatchingInfo(Double workDayMatchingRate, Double workTimeMatchingRate,
            Double workLocationMatchingRate, Double workCareTypeMatchingRate,
            Double workSalaryMatchingRate, Integer workSalaryDifference) {
        this.workDayMatchingRate = workDayMatchingRate;
        this.workTimeMatchingRate = workTimeMatchingRate;
        this.workLocationMatchingRate = workLocationMatchingRate;
        this.workCareTypeMatchingRate = workCareTypeMatchingRate;
        this.workSalaryMatchingRate = workSalaryMatchingRate;
        this.workSalaryDifference = workSalaryDifference;
    }
}
