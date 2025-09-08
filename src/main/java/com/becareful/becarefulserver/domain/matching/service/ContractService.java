package com.becareful.becarefulserver.domain.matching.service;

import static com.becareful.becarefulserver.domain.matching.domain.MatchingApplicationStatus.*;

import com.becareful.becarefulserver.domain.matching.domain.*;
import com.becareful.becarefulserver.domain.matching.repository.*;
import java.time.*;
import lombok.*;
import org.springframework.stereotype.*;
import org.springframework.transaction.annotation.*;

@Service
@RequiredArgsConstructor
public class ContractService {

    private final ContractRepository contractRepository;
    private final MatchingRepository matchingRepository;

    @Transactional
    public void createContract(Matching matching, LocalDate workStartDate) {
        matching.hire();

        Recruitment recruitment = matching.getRecruitment();
        recruitment.complete();

        matchingRepository.findAllByRecruitment(recruitment).forEach(match -> {
            if (match.getMatchingApplicationStatus().equals(지원검토중)) {
                match.failed();
            } else if (match.getMatchingApplicationStatus().equals(미지원)) {
                matchingRepository.delete(match);
            }
        });

        Contract contract = Contract.create(matching, matching.getRecruitment(), workStartDate);
        contractRepository.save(contract);
    }
}
