package com.becareful.becarefulserver.domain.matching.dto.response;

import com.becareful.becarefulserver.domain.matching.domain.Application;
import java.util.*;

public record CaregiverAppliedRecruitmentsResponse(
        List<CaregiverRecruitmentResponse> recruitments, boolean hasNewChat) {

    public static CaregiverAppliedRecruitmentsResponse of(List<Application> applications, boolean hasNewChat) {
        return new CaregiverAppliedRecruitmentsResponse(
                applications.stream()
                        .map(application -> CaregiverRecruitmentResponse.of(
                                application.getWorkApplication(), application.getRecruitment()))
                        .toList(),
                hasNewChat);
    }
}
