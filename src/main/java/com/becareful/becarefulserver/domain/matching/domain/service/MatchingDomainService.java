package com.becareful.becarefulserver.domain.matching.domain.service;

import com.becareful.becarefulserver.domain.caregiver.domain.WorkApplication;
import com.becareful.becarefulserver.domain.matching.domain.Matching;
import com.becareful.becarefulserver.domain.matching.domain.Recruitment;
import java.util.Optional;
import org.springframework.stereotype.Component;

@Component
public class MatchingDomainService {

    public Optional<Matching> createMatching(Recruitment recruitment, WorkApplication application) {
        if (!recruitment.getRecruitmentStatus().isRecruiting()) {
            return Optional.empty();
        }

        if (!application.isActive()) {
            return Optional.empty();
        }

        Matching matching = Matching.create(recruitment, application);

        return Optional.of(matching);
    }
}
