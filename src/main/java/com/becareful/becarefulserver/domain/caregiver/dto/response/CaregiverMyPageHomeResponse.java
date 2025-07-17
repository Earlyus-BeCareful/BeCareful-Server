package com.becareful.becarefulserver.domain.caregiver.dto.response;

import com.becareful.becarefulserver.domain.caregiver.domain.Career;
import com.becareful.becarefulserver.domain.caregiver.domain.Caregiver;
import com.becareful.becarefulserver.domain.caregiver.domain.WorkApplication;
import com.becareful.becarefulserver.domain.common.vo.Gender;
import com.becareful.becarefulserver.domain.work_location.dto.request.WorkLocationDto;
import java.time.LocalDate;
import java.util.List;

public record CaregiverMyPageHomeResponse(
        String name,
        Gender gender,
        String phoneNumber,
        String profileImageUrl,
        List<String> certificateNames,
        boolean isHavingCar,
        boolean isCompleteDementiaEducation,
        String careerTitle,
        LocalDate careerLastModifyDate,
        WorkApplicationResponse workApplicationInfo,
        boolean isWorkApplicationActive,
        LocalDate workApplicationLastModifyDate) {

    public static CaregiverMyPageHomeResponse of(
            Caregiver caregiver, Career career, WorkApplication workApplication, List<WorkLocationDto> locations) {
        return new CaregiverMyPageHomeResponse(
                caregiver.getName(),
                caregiver.getGender(),
                caregiver.getPhoneNumber(),
                caregiver.getProfileImageUrl(),
                caregiver.getCaregiverInfo().getCertificateNames(),
                caregiver.getCaregiverInfo().isHavingCar(),
                caregiver.getCaregiverInfo().isCompleteDementiaEducation(),
                career == null ? null : career.getTitle(),
                career == null ? null : career.getUpdateDate().toLocalDate(),
                workApplication == null ? null : WorkApplicationResponse.of(locations, workApplication),
                workApplication == null ? false : workApplication.isActive(),
                workApplication == null ? null : workApplication.getUpdateDate().toLocalDate());
    }
}
