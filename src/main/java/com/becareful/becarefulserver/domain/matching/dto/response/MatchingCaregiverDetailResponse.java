package com.becareful.becarefulserver.domain.matching.dto.response;

import com.becareful.becarefulserver.domain.caregiver.domain.Career;
import com.becareful.becarefulserver.domain.caregiver.domain.CareerDetail;
import com.becareful.becarefulserver.domain.caregiver.domain.WorkApplication;
import com.becareful.becarefulserver.domain.caregiver.dto.CaregiverDto;
import com.becareful.becarefulserver.domain.caregiver.dto.WorkApplicationDto;
import com.becareful.becarefulserver.domain.caregiver.dto.response.CareerDetailResponse;
import com.becareful.becarefulserver.domain.caregiver.dto.response.CareerResponse;
import com.becareful.becarefulserver.domain.matching.domain.Matching;
import com.becareful.becarefulserver.domain.matching.domain.MediationType;
import com.becareful.becarefulserver.domain.matching.domain.vo.MatchingResultInfo;
import com.becareful.becarefulserver.domain.matching.domain.vo.MatchingResultStatus;
import com.becareful.becarefulserver.domain.work_location.dto.request.WorkLocationDto;
import java.util.List;

public record MatchingCaregiverDetailResponse(
        Long matchingId,
        MatchingResultStatus matchingResultStatus,
        MatchingResultReasonType workLocationMatchingResultReason,
        MatchingResultReasonType workDaysMatchingResultReason,
        MatchingResultReasonType workTimeMatchingResultReason,
        CaregiverDto caregiverInfo,
        WorkApplicationDto workApplicationInfo,
        CareerResponse careerInfo,
        List<MediationType> mediationTypes,
        String mediationDescription) {

    public static MatchingCaregiverDetailResponse of(
            Matching matching, Career career, List<CareerDetail> careerDetails, List<WorkLocationDto> locations) {

        WorkApplication workApplication = matching.getWorkApplication();
        MatchingResultInfo socialWorkerMatchingResult = matching.getSocialWorkerMatchingResultInfo();
        return new MatchingCaregiverDetailResponse(
                matching.getId(),
                matching.getMatchingResultStatus(),
                socialWorkerMatchingResult.isWorkLocationMatched()
                        ? MatchingResultReasonType.MATCHED_ALL
                        : MatchingResultReasonType.NOT_MATCHED,
                socialWorkerMatchingResult.getWorkDayMatchingRate() == 0
                        ? MatchingResultReasonType.NOT_MATCHED
                        : socialWorkerMatchingResult.getWorkDayMatchingRate() < 1
                                ? MatchingResultReasonType.MATCHED_PARTIALLY
                                : MatchingResultReasonType.MATCHED_ALL,
                socialWorkerMatchingResult.isWorkTimeMatched()
                        ? MatchingResultReasonType.MATCHED_ALL
                        : MatchingResultReasonType.NOT_MATCHED,
                CaregiverDto.from(workApplication.getCaregiver()),
                WorkApplicationDto.of(locations, workApplication),
                CareerResponse.of(
                        career,
                        careerDetails.stream().map(CareerDetailResponse::from).toList()),
                matching.getMediationTypes().stream().toList(),
                matching.getMediationDescription());
    }

    private enum MatchingResultReasonType {
        MATCHED_ALL,
        MATCHED_PARTIALLY,
        NOT_MATCHED
    }
}
