package com.becareful.becarefulserver.domain.matching.service;

import static com.becareful.becarefulserver.domain.matching.domain.MatchingApplicationStatus.*;
import com.becareful.becarefulserver.domain.caregiver.domain.*;
import com.becareful.becarefulserver.domain.matching.domain.*;
import static com.becareful.becarefulserver.domain.matching.domain.MatchingApplicationStatus.*;
import com.becareful.becarefulserver.domain.matching.repository.*;
import com.becareful.becarefulserver.global.exception.*;
import com.becareful.becarefulserver.global.exception.exception.*;
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

        WorkApplication workApplication = matching.getWorkApplication();
        if (workApplication == null) {
            throw new ContractException(ErrorMessage.CONTRACT_CAREGIVER_NOT_EXISTS);
        }
        Caregiver caregiver = workApplication.getCaregiver();
        Contract contract = Contract.create(matching, matching.getRecruitment(), caregiver, workStartDate);
        contractRepository.save(contract);
    }
}
