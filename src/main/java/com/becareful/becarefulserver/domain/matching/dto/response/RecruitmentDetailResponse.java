package com.becareful.becarefulserver.domain.matching.dto.response;

import com.becareful.becarefulserver.domain.matching.domain.Recruitment;
import com.becareful.becarefulserver.domain.matching.dto.ElderlyDto;
import com.becareful.becarefulserver.domain.matching.dto.InstitutionDto;
import com.becareful.becarefulserver.domain.matching.dto.RecruitmentDto;
import com.becareful.becarefulserver.domain.nursing_institution.domain.NursingInstitution;
import com.becareful.becarefulserver.domain.socialworker.domain.Elderly;

public record RecruitmentDetailResponse(
        RecruitmentDto recruitmentInfo,
        ElderlyDto elderlyInfo,
        InstitutionDto institutionInfo,
        boolean isHotRecruitment,
        boolean isHourlySalaryTop,
        Integer matchRate) {

    public static RecruitmentDetailResponse from(
            Recruitment recruitment, boolean isHotRecruitment, boolean isHourlySalaryTop, Integer matchRate) {
        Elderly elderly = recruitment.getElderly();
        NursingInstitution institution = elderly.getNursingInstitution();

        return new RecruitmentDetailResponse(
                RecruitmentDto.from(recruitment),
                ElderlyDto.from(elderly),
                InstitutionDto.from(institution),
                isHotRecruitment,
                isHourlySalaryTop,
                matchRate);
    }
}
