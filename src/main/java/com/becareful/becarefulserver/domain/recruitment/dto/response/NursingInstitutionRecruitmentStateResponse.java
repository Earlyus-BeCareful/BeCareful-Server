package com.becareful.becarefulserver.domain.recruitment.dto.response;

import com.becareful.becarefulserver.domain.common.vo.Gender;

public record NursingInstitutionRecruitmentStateResponse(
        Long recruitmentId,
        String elderlyName,
        int elderlyAge,
        Gender gender,
        String elderlyProfileImage,
        int matchingNum,
        int applyNum
) {
}
