package com.becareful.becarefulserver.domain.matching.dto.response;

import com.becareful.becarefulserver.domain.caregiver.domain.WorkApplication;
import com.becareful.becarefulserver.domain.matching.domain.Recruitment;
import com.becareful.becarefulserver.domain.matching.domain.vo.MatchingResultStatus;
import com.becareful.becarefulserver.domain.matching.dto.RecruitmentSimpleDto;
import com.becareful.becarefulserver.global.util.MatchingUtil;
import lombok.Getter;

public record CaregiverRecruitmentResponse(
        RecruitmentSimpleDto recruitmentInfo,
        MatchingResultStatus matchingResultStatus,
        String recruitmentStatus,
        boolean isHotRecruitment,
        boolean isHourlySalaryTop) {

    public static CaregiverRecruitmentResponse of(WorkApplication workApplication, Recruitment recruitment) {
        CaregiverRecruitmentStatus recruitmentStatus =
                switch (recruitment.getRecruitmentStatus()) {
                    case 모집중, 조율중 -> CaregiverRecruitmentStatus.모집중;
                    case 모집완료, 공고마감 -> CaregiverRecruitmentStatus.마감;
                };
        return new CaregiverRecruitmentResponse(
                RecruitmentSimpleDto.from(recruitment),
                MatchingUtil.calculateMatchingStatus(workApplication, recruitment),
                recruitmentStatus.getValue(),
                // TODO : 매칭 필터 정보 추가
                false,
                false);
    }

    @Getter
    private enum CaregiverRecruitmentStatus {
        모집중("일자리 모집중"),
        마감("일자리 마감");

        CaregiverRecruitmentStatus(String value) {
            this.value = value;
        }

        private String value;
    }
}
