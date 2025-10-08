package com.becareful.becarefulserver.domain.matching.dto.response;

import com.becareful.becarefulserver.domain.matching.domain.Matching;
import com.becareful.becarefulserver.domain.matching.domain.MatchingStatus;
import java.util.*;

public record CaregiverAppliedRecruitmentsResponse(List<Item> recruitments, boolean hasNewChat) {
    public record Item(CaregiverRecruitmentResponse recruitmentInfo, MatchingStatus matchingStatus) {
        public static Item from(Matching matching) {
            return new Item(CaregiverRecruitmentResponse.from(matching), matching.getMatchingStatus());
        }
    }

    public static CaregiverAppliedRecruitmentsResponse of(List<Item> recruitments, boolean hasNewChat) {
        return new CaregiverAppliedRecruitmentsResponse(recruitments, hasNewChat);
    }
}
