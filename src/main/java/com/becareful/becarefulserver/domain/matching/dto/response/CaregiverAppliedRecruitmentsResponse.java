package com.becareful.becarefulserver.domain.matching.dto.response;

import com.becareful.becarefulserver.domain.matching.domain.Matching;
import com.becareful.becarefulserver.domain.matching.domain.MatchingApplicationStatus;
import java.util.*;

public record CaregiverAppliedRecruitmentsResponse(List<Item> recruitments, boolean hasNewChat) {
    public record Item(
            RecruitmentListItemResponse recruitmentInfo, MatchingApplicationStatus matchingApplicationStatus) {
        public static Item from(Matching matching) {
            return new Item(RecruitmentListItemResponse.from(matching), matching.getMatchingApplicationStatus());
        }
    }

    public static CaregiverAppliedRecruitmentsResponse of(List<Item> recruitments, boolean hasNewChat) {
        return new CaregiverAppliedRecruitmentsResponse(recruitments, hasNewChat);
    }
}
