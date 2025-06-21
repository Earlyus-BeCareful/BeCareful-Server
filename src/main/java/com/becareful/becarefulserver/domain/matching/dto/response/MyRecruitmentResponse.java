package com.becareful.becarefulserver.domain.matching.dto.response;

import com.becareful.becarefulserver.domain.matching.domain.MatchingStatus;
import com.becareful.becarefulserver.domain.matching.domain.Recruitment;
import com.becareful.becarefulserver.domain.matching.domain.vo.MatchingResultStatus;
import com.becareful.becarefulserver.domain.matching.dto.RecruitmentDto;

import java.time.format.DateTimeFormatter;

public record MyRecruitmentResponse(CaregiverRecruitmentResponse recruitmentInfo, MatchingStatus matchingStatus) {
    public static MyRecruitmentResponse of(Recruitment recruitment, MatchingStatus matchingStatus) {
        return new MyRecruitmentResponse(
                new CaregiverRecruitmentResponse(
                        RecruitmentDto.from(recruitment),
                        MatchingResultStatus.보통, // TODO : 매칭율 계산 후 외부에서 주입
                        false,
                        false),
                matchingStatus);
    }
}
