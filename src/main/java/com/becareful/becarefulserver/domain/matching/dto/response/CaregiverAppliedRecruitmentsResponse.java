package com.becareful.becarefulserver.domain.matching.dto.response;

import com.becareful.becarefulserver.domain.matching.domain.Matching;
import java.util.*;

public record CaregiverAppliedRecruitmentsResponse(List<Item> recruitments, boolean hasNewChat) {

    public static CaregiverAppliedRecruitmentsResponse of(List<Matching> matchings, boolean hasNewChat) {
        return new CaregiverAppliedRecruitmentsResponse(
                matchings.stream().map(Item::from).toList(), hasNewChat);
    }

    private record Item(CaregiverRecruitmentResponse recruitmentInfo) {
        public static Item from(Matching matching) {
            return new Item(CaregiverRecruitmentResponse.from(matching));
        }
    }
}
