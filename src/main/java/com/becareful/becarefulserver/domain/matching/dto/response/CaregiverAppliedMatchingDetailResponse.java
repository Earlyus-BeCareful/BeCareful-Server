package com.becareful.becarefulserver.domain.matching.dto.response;

import com.becareful.becarefulserver.domain.matching.domain.Recruitment;
import com.becareful.becarefulserver.domain.matching.dto.InstitutionDto;
import com.becareful.becarefulserver.domain.matching.dto.RecruitmentDto;
import com.becareful.becarefulserver.domain.nursing_institution.domain.NursingInstitution;
import com.becareful.becarefulserver.domain.matching.dto.ElderlyDto;
import com.becareful.becarefulserver.domain.socialworker.domain.Elderly;
import java.time.LocalDate;

public record CaregiverAppliedMatchingDetailResponse(RecruitmentDetailResponse recruitmentDetailInfo, LocalDate applyDate) {

    public static CaregiverAppliedMatchingDetailResponse of(
            Recruitment recruitment,
            boolean isHotRecruitment,
            boolean isHourlySalaryTop,
            Integer matchRate,
            LocalDate applyDate) {

        Elderly elderly = recruitment.getElderly();
        NursingInstitution institution = elderly.getNursingInstitution();

        return new CaregiverAppliedMatchingDetailResponse(
                new RecruitmentDetailResponse(
                        RecruitmentDto.from(recruitment),
                        ElderlyDto.from(elderly),
                        InstitutionDto.from(institution),
                        isHotRecruitment,
                        isHourlySalaryTop,
                        matchRate),
                applyDate);
    }
}
