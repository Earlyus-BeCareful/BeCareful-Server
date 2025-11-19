package com.becareful.becarefulserver.global.util;

import com.becareful.becarefulserver.domain.caregiver.domain.WorkApplication;
import com.becareful.becarefulserver.domain.matching.domain.Recruitment;
import com.becareful.becarefulserver.domain.matching.domain.vo.MatchingResultStatus;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class MatchingUtil {

    public static MatchingResultStatus calculateMatchingRate(WorkApplication application, Recruitment recruitment) {
        // TODO
        return MatchingResultStatus.제외;
    }
}
