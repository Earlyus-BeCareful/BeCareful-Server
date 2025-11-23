package com.becareful.becarefulserver.domain.matching.dto.response;

import com.becareful.becarefulserver.domain.caregiver.domain.Career;
import com.becareful.becarefulserver.domain.caregiver.domain.CareerDetail;
import com.becareful.becarefulserver.domain.caregiver.domain.WorkApplication;
import com.becareful.becarefulserver.domain.caregiver.dto.CareerDto;
import com.becareful.becarefulserver.domain.caregiver.dto.CaregiverDto;
import com.becareful.becarefulserver.domain.caregiver.dto.WorkApplicationDto;
import com.becareful.becarefulserver.domain.matching.domain.MediationType;
import com.becareful.becarefulserver.domain.matching.domain.vo.MatchingResultInfo;
import com.becareful.becarefulserver.domain.matching.domain.vo.MatchingResultStatus;
import java.util.List;

public record MatchingCaregiverDetailResponse(
        Long matchingId, // TODO : 필드 제거
        MatchingResultStatus matchingResultStatus,
        MatchingResultReasonType workLocationMatchingResultReason,
        MatchingResultReasonType workDaysMatchingResultReason,
        MatchingResultReasonType workTimeMatchingResultReason,
        CaregiverDto caregiverInfo,
        WorkApplicationDto workApplicationInfo,
        CareerDto careerInfo,
        List<MediationType> mediationTypes,
        String mediationDescription) {

    public static MatchingCaregiverDetailResponse of(
            WorkApplication workApplication,
            MatchingResultStatus matchingResultStatus,
            MatchingResultInfo matchingResultInfo,
            Career career,
            List<CareerDetail> careerDetails,
            List<MediationType> mediationTypes,
            String mediationDescription) {
        return new MatchingCaregiverDetailResponse(
                0L, // TODO : 필드 제거
                matchingResultStatus,
                matchingResultInfo.isWorkLocationMatched()
                        ? MatchingResultReasonType.MATCHED_ALL
                        : MatchingResultReasonType.NOT_MATCHED,
                matchingResultInfo.getWorkDayMatchingRate() == 0
                        ? MatchingResultReasonType.NOT_MATCHED
                        : matchingResultInfo.getWorkDayMatchingRate() < 1
                                ? MatchingResultReasonType.MATCHED_PARTIALLY
                                : MatchingResultReasonType.MATCHED_ALL,
                matchingResultInfo.isWorkTimeMatched()
                        ? MatchingResultReasonType.MATCHED_ALL
                        : MatchingResultReasonType.NOT_MATCHED,
                CaregiverDto.from(workApplication.getCaregiver()),
                WorkApplicationDto.from(workApplication),
                career != null ? CareerDto.of(career, careerDetails) : null,
                mediationTypes,
                mediationDescription);
    }

    private enum MatchingResultReasonType {
        MATCHED_ALL,
        MATCHED_PARTIALLY,
        NOT_MATCHED
    }
}
