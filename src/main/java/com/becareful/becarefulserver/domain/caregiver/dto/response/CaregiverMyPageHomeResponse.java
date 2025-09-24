package com.becareful.becarefulserver.domain.caregiver.dto.response;

import com.becareful.becarefulserver.domain.caregiver.domain.Career;
import com.becareful.becarefulserver.domain.caregiver.domain.Caregiver;
import com.becareful.becarefulserver.domain.caregiver.domain.WorkApplication;
import com.becareful.becarefulserver.domain.caregiver.dto.CareerSimpleDto;
import com.becareful.becarefulserver.domain.caregiver.dto.CaregiverDto;
import com.becareful.becarefulserver.domain.caregiver.dto.WorkApplicationDto;

public record CaregiverMyPageHomeResponse(
        CaregiverDto caregiverInfo, CareerSimpleDto careerInfo, WorkApplicationDto workApplicationInfo) {

    public static CaregiverMyPageHomeResponse of(Caregiver caregiver, Career career, WorkApplication workApplication) {
        return new CaregiverMyPageHomeResponse(
                CaregiverDto.from(caregiver),
                career == null ? null : CareerSimpleDto.from(career),
                workApplication == null ? null : WorkApplicationDto.from(workApplication));
    }
}
