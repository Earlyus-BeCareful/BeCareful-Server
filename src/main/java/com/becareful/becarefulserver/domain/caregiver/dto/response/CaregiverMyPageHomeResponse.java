package com.becareful.becarefulserver.domain.caregiver.dto.response;

import com.becareful.becarefulserver.domain.caregiver.domain.CareerDetail;
import com.becareful.becarefulserver.domain.caregiver.domain.Caregiver;
import com.becareful.becarefulserver.domain.caregiver.domain.WorkApplication;
import com.becareful.becarefulserver.domain.caregiver.dto.CareerDto;
import com.becareful.becarefulserver.domain.caregiver.dto.CaregiverDto;
import com.becareful.becarefulserver.domain.caregiver.dto.WorkApplicationDto;
import java.util.List;

public record CaregiverMyPageHomeResponse(
        CaregiverDto caregiverInfo, CareerDto careerInfo, WorkApplicationDto workApplicationInfo) {

    public static CaregiverMyPageHomeResponse of(
            Caregiver caregiver, List<CareerDetail> careerDetails, WorkApplication workApplication) {
        return new CaregiverMyPageHomeResponse(
                CaregiverDto.from(caregiver),
                careerDetails.isEmpty() ? null : CareerDto.from(careerDetails),
                workApplication == null ? null : WorkApplicationDto.from(workApplication));
    }
}
