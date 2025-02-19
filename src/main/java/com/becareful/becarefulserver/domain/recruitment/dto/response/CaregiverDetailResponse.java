package com.becareful.becarefulserver.domain.recruitment.dto.response;

import com.becareful.becarefulserver.domain.caregiver.domain.Career;
import com.becareful.becarefulserver.domain.caregiver.domain.CareerDetail;
import com.becareful.becarefulserver.domain.caregiver.domain.WorkTime;
import com.becareful.becarefulserver.domain.caregiver.dto.response.CareerDetailResponse;
import com.becareful.becarefulserver.domain.caregiver.dto.response.CareerResponse;
import com.becareful.becarefulserver.domain.common.domain.CareType;
import com.becareful.becarefulserver.domain.recruitment.domain.Matching;
import com.becareful.becarefulserver.domain.recruitment.domain.MediationType;

import java.time.DayOfWeek;
import java.util.List;
import lombok.Builder;

@Builder
public record CaregiverDetailResponse(
        String name,
        String profileImageUrl,
        List<DayOfWeek> workDays,
        List<WorkTime> workTimes,
        Integer workSalaryAmount,
        List<CareType> careTypes,
        List<String> certificates,
        boolean isHavingCar,
        boolean isCompleteDementiaEducation,
        CareerResponse careerInfo,
        List<MediationType> mediationTypes,
        String mediationDescription
) {

    public static CaregiverDetailResponse of(Matching matching, Career career, List<CareerDetail> careerDetails) {
        return CaregiverDetailResponse.builder()
                .name(matching.getWorkApplication().getCaregiver().getName())
                .profileImageUrl(matching.getWorkApplication().getCaregiver().getProfileImageUrl())
                .workDays(matching.getWorkApplication().getWorkDays().stream().toList())
                .workTimes(matching.getWorkApplication().getWorkTimes().stream().toList())
                .workSalaryAmount(matching.getWorkApplication().getWorkSalaryAmount())
                .careTypes(matching.getWorkApplication().getWorkCareTypes().stream().toList())
                .certificates(matching.getWorkApplication().getCaregiver().getCaregiverInfo().getCertificateNames())
                .isHavingCar(matching.getWorkApplication().getCaregiver().getCaregiverInfo().isHavingCar())
                .isCompleteDementiaEducation(matching.getWorkApplication().getCaregiver().getCaregiverInfo().isCompleteDementiaEducation())
                .careerInfo(career != null ? CareerResponse.of(career, careerDetails.stream().map(
                        CareerDetailResponse::from).toList()) : null)
                .build();
    }
}
