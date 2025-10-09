package com.becareful.becarefulserver.domain.matching.domain.service;

import static com.becareful.becarefulserver.domain.matching.domain.vo.MatchingResultStatus.제외;

import com.becareful.becarefulserver.domain.caregiver.domain.WorkApplication;
import com.becareful.becarefulserver.domain.matching.domain.Matching;
import com.becareful.becarefulserver.domain.matching.domain.Recruitment;
import com.becareful.becarefulserver.domain.matching.domain.vo.MatchingResultStatus;
import java.util.Optional;
import org.springframework.stereotype.Component;

@Component
public class MatchingDomainService {

    public Optional<Matching> createMatching(Recruitment recruitment, WorkApplication application) {
        Matching matching = Matching.create(recruitment, application);
        MatchingResultStatus result = matching.getMatchingResultStatus();

        if (result.equals(제외)) {
            return Optional.empty();
        }

        return Optional.of(matching);
    }
}
