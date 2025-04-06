package com.becareful.becarefulserver.domain.matching.dto.response;

import com.becareful.becarefulserver.domain.matching.domain.Recruitment;
import com.becareful.becarefulserver.domain.socialworker.domain.Elderly;
import com.becareful.becarefulserver.domain.socialworker.domain.NursingInstitution;

import java.time.LocalDate;

public record MyRecruitmentDetailResponse(
        RecruitmentDetailResponse recruitmentDetailInfo,
        LocalDate applyDate
) {

    public static MyRecruitmentDetailResponse of(
            Recruitment recruitment,
            boolean isHotRecruitment,
            boolean isHourlySalaryTop,
            Integer matchRate,
            LocalDate applyDate) {

        Elderly elderly = recruitment.getElderly();
        NursingInstitution institution = elderly.getNursingInstitution();

        return new MyRecruitmentDetailResponse(
                new RecruitmentDetailResponse(
                        RecruitmentInfoResponse.from(recruitment, elderly),
                        ElderlyInfoResponse.from(elderly),
                        InstitutionInfoResponse.from(institution),
                        isHotRecruitment,
                        isHourlySalaryTop,
                        matchRate
                ),
                applyDate
        );
    }
}
