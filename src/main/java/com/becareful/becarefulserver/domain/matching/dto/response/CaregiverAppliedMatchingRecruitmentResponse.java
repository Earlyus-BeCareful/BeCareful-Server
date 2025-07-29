package com.becareful.becarefulserver.domain.matching.dto.response;

import com.becareful.becarefulserver.domain.matching.domain.Matching;
import com.becareful.becarefulserver.domain.matching.domain.MatchingApplicationStatus;

public record CaregiverAppliedMatchingRecruitmentResponse(
        RecruitmentListItemResponse recruitmentInfo, MatchingApplicationStatus matchingApplicationStatus) {
    public static CaregiverAppliedMatchingRecruitmentResponse from(Matching matching) {
        return new CaregiverAppliedMatchingRecruitmentResponse(
                RecruitmentListItemResponse.from(matching), matching.getMatchingApplicationStatus());
    }
}
