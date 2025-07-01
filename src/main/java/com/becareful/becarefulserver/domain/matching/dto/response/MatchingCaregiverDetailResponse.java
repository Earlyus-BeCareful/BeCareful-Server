package com.becareful.becarefulserver.domain.matching.dto.response;

import com.becareful.becarefulserver.domain.caregiver.domain.Career;
import com.becareful.becarefulserver.domain.caregiver.domain.CareerDetail;
import com.becareful.becarefulserver.domain.caregiver.domain.WorkApplication;
import com.becareful.becarefulserver.domain.caregiver.dto.WorkApplicationDto;
import com.becareful.becarefulserver.domain.caregiver.dto.response.CareerDetailResponse;
import com.becareful.becarefulserver.domain.caregiver.dto.response.CareerResponse;
import com.becareful.becarefulserver.domain.matching.domain.Matching;
import com.becareful.becarefulserver.domain.matching.domain.MediationType;
import com.becareful.becarefulserver.domain.matching.domain.vo.MatchingResultStatus;
import com.becareful.becarefulserver.domain.matching.dto.CaregiverSimpleDto;
import java.util.List;

public record MatchingCaregiverDetailResponse(
        Long matchingId,
        MatchingResultStatus matchingResultStatus,
        CaregiverSimpleDto caregiverInfo,
        WorkApplicationDto caregiverWorkApplicationInfo,
        CareerResponse careerInfo,
        List<MediationType> mediationTypes,
        String mediationDescription) {

    public static MatchingCaregiverDetailResponse of(
            Matching matching, Career career, List<CareerDetail> careerDetails) {

        WorkApplication workApplication = matching.getWorkApplication();
        return new MatchingCaregiverDetailResponse(
                matching.getId(),
                matching.getMatchingResultStatus(),
                CaregiverSimpleDto.of(workApplication.getCaregiver(), career),
                WorkApplicationDto.from(workApplication),
                CareerResponse.of(
                        career,
                        careerDetails.stream().map(CareerDetailResponse::from).toList()),
                matching.getMediationTypes().stream().toList(),
                matching.getMediationDescription());
    }
}
