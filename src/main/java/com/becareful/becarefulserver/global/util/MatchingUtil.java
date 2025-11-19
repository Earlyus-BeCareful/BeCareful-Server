package com.becareful.becarefulserver.global.util;

import com.becareful.becarefulserver.domain.caregiver.domain.WorkApplication;
import com.becareful.becarefulserver.domain.caregiver.domain.WorkTime;
import com.becareful.becarefulserver.domain.common.domain.vo.Location;
import com.becareful.becarefulserver.domain.matching.domain.Recruitment;
import com.becareful.becarefulserver.domain.matching.domain.vo.MatchingResultInfo;
import com.becareful.becarefulserver.domain.matching.domain.vo.MatchingResultStatus;
import java.time.DayOfWeek;
import java.util.EnumSet;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class MatchingUtil {

    public static MatchingResultStatus calculateMatchingStatus(WorkApplication application, Recruitment recruitment) {
        MatchingResultInfo matchingResultInfo = calculateMatchingRate(application, recruitment);
        if (!matchingResultInfo.isWorkLocationMatched()) {
            return MatchingResultStatus.제외;
        }

        if (matchingResultInfo.getWorkDayMatchingRate() >= 0.7 && matchingResultInfo.isWorkTimeMatched()) {
            return MatchingResultStatus.높음;
        }

        if (matchingResultInfo.getWorkDayMatchingRate() >= 0.5 || matchingResultInfo.isWorkTimeMatched()) {
            return MatchingResultStatus.보통;
        }

        return MatchingResultStatus.낮음;
    }

    /**
     * @param recruitment       - 사회복지사가 등록한 공고
     * @param workApplication   - 요양보호사가 등록한 지원서
     * @return                  - MatchingInfo
     */
    public static MatchingResultInfo calculateMatchingRate(WorkApplication workApplication, Recruitment recruitment) {
        boolean workLocationMatchingRate =
                isWorkLocationMatched(recruitment.getResidentialLocation(), workApplication.getWorkLocations());
        Double workDayMatchingRate = calculateDayMatchingRate(recruitment.getWorkDays(), workApplication.getWorkDays());
        boolean workTimeMatchingRate = isWorkTimeMatched(recruitment.getWorkTimes(), workApplication.getWorkTimes());

        return MatchingResultInfo.create(workLocationMatchingRate, workDayMatchingRate, workTimeMatchingRate);
    }

    private static boolean isWorkLocationMatched(Location residentialLocation, List<Location> workableLocations) {
        for (Location location : workableLocations) {
            if (location.matches(residentialLocation)) {
                return true;
            }
        }
        return false;
    }

    private static Double calculateDayMatchingRate(EnumSet<DayOfWeek> recruitmentDays, EnumSet<DayOfWeek> applyDays) {
        EnumSet<DayOfWeek> intersection = EnumSet.copyOf(recruitmentDays);
        intersection.retainAll(applyDays);

        return ((double) intersection.size() / recruitmentDays.size()) * 100;
    }

    private static boolean isWorkTimeMatched(EnumSet<WorkTime> recruitmentTimes, EnumSet<WorkTime> applyTimes) {
        EnumSet<WorkTime> intersection = EnumSet.copyOf(recruitmentTimes);
        intersection.retainAll(applyTimes);
        return ((double) intersection.size() / recruitmentTimes.size()) * 100 == 100;
    }
}
