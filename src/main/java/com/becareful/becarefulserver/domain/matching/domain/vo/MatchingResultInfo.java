package com.becareful.becarefulserver.domain.matching.domain.vo;

import jakarta.persistence.Embeddable;
import lombok.*;

@ToString
@Getter
@Embeddable
@Builder(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MatchingResultInfo {

    private boolean isWorkLocationMatched;
    private Double workDayMatchingRate;
    private boolean isWorkTimeMatched;

    public static MatchingResultInfo create(boolean isWorkLocationMatched, Double workDayMatchingRate, boolean isWorkTimeMatched) {
        return MatchingResultInfo.builder()
                .isWorkLocationMatched(isWorkLocationMatched)
                .workDayMatchingRate(workDayMatchingRate)
                .isWorkTimeMatched(isWorkTimeMatched)
                .build();
    }

    public MatchingResultStatus judgeMatchingResultStatus() {
        if (!isWorkLocationMatched()) {
            return MatchingResultStatus.제외;
        }

        if (getWorkDayMatchingRate() >= 0.7 && isWorkTimeMatched()) {
            return MatchingResultStatus.높음;
        }

        if (getWorkDayMatchingRate() >= 0.5 || isWorkTimeMatched()) {
            return MatchingResultStatus.보통;
        }

        return MatchingResultStatus.낮음;
    }
}
