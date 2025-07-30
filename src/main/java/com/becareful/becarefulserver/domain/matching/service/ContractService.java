package com.becareful.becarefulserver.domain.matching.service;

import static com.becareful.becarefulserver.domain.matching.domain.MatchingApplicationStatus.*;
import static com.becareful.becarefulserver.global.exception.ErrorMessage.MATCHING_NOT_EXISTS;

import com.becareful.becarefulserver.domain.matching.domain.Contract;
import com.becareful.becarefulserver.domain.matching.domain.Matching;
import com.becareful.becarefulserver.domain.matching.domain.Recruitment;
import com.becareful.becarefulserver.domain.matching.repository.ContractRepository;
import com.becareful.becarefulserver.domain.matching.repository.MatchingRepository;
import com.becareful.becarefulserver.global.exception.exception.MatchingException;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ContractService {

    private final ContractRepository contractRepository;
    private final MatchingRepository matchingRepository;

    @Transactional
    public void createContract(Long matchingId, LocalDate workStartDate) {
        Matching matching =
                matchingRepository.findById(matchingId).orElseThrow(() -> new MatchingException(MATCHING_NOT_EXISTS));

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
