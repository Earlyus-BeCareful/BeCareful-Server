package com.becareful.becarefulserver.domain.matching.service;

import com.becareful.becarefulserver.domain.matching.domain.Contract;
import com.becareful.becarefulserver.domain.matching.domain.Matching;
import com.becareful.becarefulserver.domain.matching.domain.MatchingApplicationStatus;
import com.becareful.becarefulserver.domain.matching.domain.Recruitment;
import com.becareful.becarefulserver.domain.matching.dto.request.ContractEditRequest;
import com.becareful.becarefulserver.domain.matching.dto.response.ContractDetailResponse;
import com.becareful.becarefulserver.domain.matching.dto.response.ContractInfoListResponse;
import com.becareful.becarefulserver.domain.matching.repository.ContractRepository;
import com.becareful.becarefulserver.domain.matching.repository.MatchingRepository;
import com.becareful.becarefulserver.domain.matching.repository.RecruitmentRepository;
import jakarta.persistence.EntityNotFoundException;
import java.time.LocalDate;
import java.util.EnumSet;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ContractService {
    private final ContractRepository contractRepository;
    private final MatchingRepository matchingRepository;
    private final RecruitmentRepository recruitmentRepository;

    @Transactional
    public void createContract(Long matchingId, LocalDate workStartDate) {
        Matching matching = matchingRepository
                .findById(matchingId)
                .orElseThrow(() -> new IllegalArgumentException("Matching not found"));

        matching.hire();
        matchingRepository.save(matching);

        Recruitment recruitment = matching.getRecruitment();
        recruitment.complete();
        recruitmentRepository.save(recruitment);

        matchingRepository.findByRecruitment(recruitment).stream()
                .filter(match -> match.getMatchingApplicationStatus().equals(MatchingApplicationStatus.지원))
                .forEach(match -> {
                    match.failed();
                    matchingRepository.save(match);
                });

        Contract contract = Contract.create(matching, matching.getRecruitment(), workStartDate);
        contractRepository.save(contract);
    }

    @Transactional(readOnly = true)
    public ContractInfoListResponse getContractListAndInfo(Long matchingId) {
        List<Contract> contracts = contractRepository.findByMatchingIdOrderByCreateDateAsc(matchingId);
        Matching matching = matchingRepository
                .findById(matchingId)
                .orElseThrow(() -> new EntityNotFoundException("Matching not found with id: " + matchingId));

        return ContractInfoListResponse.of(matching, contracts);
    }

    @Transactional
    public void editContract(ContractEditRequest request) {

        Matching matching = matchingRepository
                .findById(request.matchingId())
                .orElseThrow(() -> new IllegalArgumentException("Matching not found"));

        Contract contract = Contract.edit(
                matching,
                EnumSet.copyOf(request.workDays()),
                request.workStartTime(),
                request.workEndTime(),
                request.workSalaryUnitType(),
                request.workSalaryAmount(),
                request.workStartDate(),
                EnumSet.copyOf(request.careTypes()));
        contractRepository.save(contract);
    }

    // 직전 계약서 내용 불러오기
    @Transactional(readOnly = true)
    public ContractDetailResponse getContractDetail(Long contractId) {
        Contract contract = contractRepository
                .findById(contractId)
                .orElseThrow(() -> new IllegalArgumentException("Contract not found"));

        return ContractDetailResponse.from(
                contract.getMatching().getRecruitment().getElderly(),
                contract.getWorkDays().stream().toList(),
                contract.getWorkStartTime(),
                contract.getWorkEndTime(),
                contract.getWorkSalaryAmount(),
                contract.getWorkStartDate());
    }
}
