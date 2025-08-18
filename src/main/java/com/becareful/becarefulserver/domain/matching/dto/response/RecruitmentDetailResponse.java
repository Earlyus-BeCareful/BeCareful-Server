package com.becareful.becarefulserver.domain.matching.dto.response;

import com.becareful.becarefulserver.domain.matching.domain.Matching;
import com.becareful.becarefulserver.domain.matching.domain.Recruitment;
import com.becareful.becarefulserver.domain.matching.domain.vo.MatchingResultStatus;
import com.becareful.becarefulserver.domain.matching.dto.ElderlyDto;
import com.becareful.becarefulserver.domain.matching.dto.InstitutionSimpleDto;
import com.becareful.becarefulserver.domain.matching.dto.RecruitmentDto;
import com.becareful.becarefulserver.domain.nursing_institution.domain.NursingInstitution;
import com.becareful.becarefulserver.domain.socialworker.domain.Elderly;

public record RecruitmentDetailResponse(
        RecruitmentDto recruitmentInfo,
        ElderlyDto elderlyInfo,
        InstitutionSimpleDto institutionInfo,
        MatchingResultStatus matchingResultStatus,
        boolean isHotRecruitment,
        boolean isHourlySalaryTop) {

    public static RecruitmentDetailResponse from(
            Matching matching, boolean isHotRecruitment, boolean isHourlySalaryTop) {
        Recruitment recruitment = matching.getRecruitment();
        Elderly elderly = recruitment.getElderly();
        NursingInstitution institution = elderly.getNursingInstitution();

        return new RecruitmentDetailResponse(
                RecruitmentDto.from(recruitment),
                ElderlyDto.from(elderly),
                InstitutionSimpleDto.from(institution),
                matching.getMatchingResultStatus(),
                isHotRecruitment,
                isHourlySalaryTop);
    }
}
