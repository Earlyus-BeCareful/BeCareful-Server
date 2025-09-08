package com.becareful.becarefulserver.domain.matching.dto.response;

import com.becareful.becarefulserver.domain.matching.domain.Matching;
import com.becareful.becarefulserver.domain.matching.domain.MatchingApplicationStatus;
import java.util.*;

public record CaregiverAppliedMatchingRecruitmentsResponse(List<Item> recruitments, boolean hasNewChat) {
    public record Item(
            RecruitmentListItemResponse recruitmentInfo, MatchingApplicationStatus matchingApplicationStatus) {
        public static Item from(Matching matching) {
            return new Item(RecruitmentListItemResponse.from(matching), matching.getMatchingApplicationStatus());
        }
    }

    public static CaregiverAppliedMatchingRecruitmentsResponse of(List<Item> recruitments, boolean hasNewChat) {
        return new CaregiverAppliedMatchingRecruitmentsResponse(recruitments, hasNewChat);
    }
}
