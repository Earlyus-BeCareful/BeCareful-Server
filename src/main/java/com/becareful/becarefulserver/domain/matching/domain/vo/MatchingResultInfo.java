package com.becareful.becarefulserver.domain.matching.domain.vo;

import jakarta.persistence.Embeddable;
import lombok.*;

@ToString
@Getter
@Embeddable
@Builder(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MatchingResultInfo { // TODO : 이름을 DTO 로 바꿔도 될 듯

    private boolean isWorkLocationMatched;
    private Double workDayMatchingRate;
    private boolean isWorkTimeMatched;

    public static MatchingResultInfo create(
            boolean isWorkLocationMatched, Double workDayMatchingRate, boolean isWorkTimeMatched) {
        return MatchingResultInfo.builder()
                .isWorkLocationMatched(isWorkLocationMatched)
                .workDayMatchingRate(workDayMatchingRate)
                .isWorkTimeMatched(isWorkTimeMatched)
                .build();
    }
}
