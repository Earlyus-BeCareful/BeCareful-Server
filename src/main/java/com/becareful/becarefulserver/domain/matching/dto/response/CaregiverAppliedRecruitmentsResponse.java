package com.becareful.becarefulserver.domain.matching.dto.response;

import java.util.*;

public record CaregiverAppliedRecruitmentsResponse(
        List<CaregiverRecruitmentResponse> recruitments, boolean hasNewChat) {

    public static CaregiverAppliedRecruitmentsResponse of(
            List<CaregiverRecruitmentResponse> recruitments, boolean hasNewChat) {
        return new CaregiverAppliedRecruitmentsResponse(recruitments, hasNewChat);
    }
}
