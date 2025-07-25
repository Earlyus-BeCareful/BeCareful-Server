package com.becareful.becarefulserver.domain.matching.dto.response;

import com.becareful.becarefulserver.domain.matching.domain.Matching;
import com.becareful.becarefulserver.domain.matching.domain.MatchingApplicationStatus;

public record CaregiverAppliedMatchingRecruitmentResponse(
        CaregiverMatchingRecruitmentResponse recruitmentInfo, MatchingApplicationStatus matchingApplicationStatus) {
    public static CaregiverAppliedMatchingRecruitmentResponse from(Matching matching) {
        return new CaregiverAppliedMatchingRecruitmentResponse(
                CaregiverMatchingRecruitmentResponse.from(matching), matching.getMatchingApplicationStatus());
    }
}
