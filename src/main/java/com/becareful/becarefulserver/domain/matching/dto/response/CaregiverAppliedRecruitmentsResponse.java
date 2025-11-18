package com.becareful.becarefulserver.domain.matching.dto.response;

import com.becareful.becarefulserver.domain.matching.domain.Matching;
import java.util.*;

public record CaregiverAppliedRecruitmentsResponse(
        List<CaregiverRecruitmentResponse> recruitments, boolean hasNewChat) {

    public static CaregiverAppliedRecruitmentsResponse of(List<Matching> matchings, boolean hasNewChat) {
        return new CaregiverAppliedRecruitmentsResponse(
                matchings.stream().map(CaregiverRecruitmentResponse::from).toList(), hasNewChat);
    }
}
