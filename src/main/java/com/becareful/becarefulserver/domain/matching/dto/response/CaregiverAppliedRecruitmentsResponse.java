package com.becareful.becarefulserver.domain.matching.dto.response;

import java.util.*;

public record CaregiverAppliedRecruitmentsResponse(List<CaregiverRecruitmentResponse> recruitments) {

    public static CaregiverAppliedRecruitmentsResponse of(List<CaregiverRecruitmentResponse> recruitments) {
        return new CaregiverAppliedRecruitmentsResponse(recruitments);
    }
}
