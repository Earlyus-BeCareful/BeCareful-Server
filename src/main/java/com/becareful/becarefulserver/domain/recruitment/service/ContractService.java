package com.becareful.becarefulserver.domain.recruitment.service;

import com.becareful.becarefulserver.domain.recruitment.domain.Contract;
import com.becareful.becarefulserver.domain.recruitment.domain.Matching;
import com.becareful.becarefulserver.domain.recruitment.domain.MatchingStatus;
import com.becareful.becarefulserver.domain.recruitment.domain.Recruitment;
import com.becareful.becarefulserver.domain.recruitment.dto.request.ContractEditRequest;
import com.becareful.becarefulserver.domain.recruitment.dto.response.ContractDetailResponse;
import com.becareful.becarefulserver.domain.recruitment.dto.response.ContractInfoResponseList;
import com.becareful.becarefulserver.domain.recruitment.repository.ContractRepository;
import com.becareful.becarefulserver.domain.recruitment.repository.MatchingRepository;
import com.becareful.becarefulserver.domain.recruitment.repository.RecruitmentRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.EnumSet;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ContractService {
    private final ContractRepository contractRepository;
    private final MatchingRepository matchingRepository;
    private final RecruitmentRepository recruitmentRepository;

    @Transactional
    public void createContract(Long matchingId,  LocalDate workStartDate) {
        Matching matching = matchingRepository.findById(matchingId)
                .orElseThrow(() -> new IllegalArgumentException("Matching not found"));

        matching.hire();
        matchingRepository.save(matching);

        Recruitment recruitment = matching.getRecruitment();
        recruitment.complete();
        recruitmentRepository.save(recruitment);

        matchingRepository.findByRecruitment(recruitment).stream()
                .filter(match -> match.getMatchingStatus().equals(MatchingStatus.지원))
                .forEach(match -> {
                    match.failed();
                    matchingRepository.save(match);
                });

        Contract contract = Contract.create(matching, matching.getRecruitment(), workStartDate);
        contractRepository.save(contract);
    }

    @Transactional(readOnly = true)
    public ContractInfoResponseList getContractListAndInfo(Long matchingId){
        List<Contract> contracts = contractRepository.findByMatchingIdOrderByCreateDateAsc(matchingId);
        Matching matching = matchingRepository.findById(matchingId)
                .orElseThrow(() -> new EntityNotFoundException("Matching not found with id: " + matchingId));


        String elderlyName = matching.getRecruitment().getElderly().getName();
        String institutionName = matching.getRecruitment().getElderly().getNursingInstitution().getName();
        String caregiverName = matching.getWorkApplication().getCaregiver().getName();


         List<ContractInfoResponseList.ContractInfoResponse> contractList = contracts.stream()
                                                .map(contract -> new ContractInfoResponseList.ContractInfoResponse(
                                                        contract.getCareTypes().stream().toList(),
                                                        contract.getWorkDays().stream().toList(),
                                                        contract.getWorkStartTime(),
                                                        contract.getWorkEndTime(),
                                                        contract.getWorkSalaryAmount(),
                                                        contract.getWorkStartDate()
                                                ))
                                                .toList();
         return new ContractInfoResponseList(elderlyName, institutionName, caregiverName, contractList);
    }

    @Transactional
    public void editContract(ContractEditRequest request){

        Matching matching = matchingRepository.findById(request.matchingId())
                .orElseThrow(() -> new IllegalArgumentException("Matching not found"));

        Contract contract = Contract.edit(matching, EnumSet.copyOf(request.workDays()),request.workStartTime(), request.workEndTime(),
                                            request.workSalaryType(), request.workSalaryAmount(),
                                            request.workStartDate(),EnumSet.copyOf(request.careTypes()));
        contractRepository.save(contract);
    }

    //직전 계약서 내용 불러오기
    @Transactional(readOnly = true)
    public ContractDetailResponse getContractDetail(Long contractId) {
        Contract contract = contractRepository.findById(contractId)
                .orElseThrow(() -> new IllegalArgumentException("Contract not found"));

        return ContractDetailResponse.from(
                contract.getMatching().getRecruitment().getElderly(),
                contract.getWorkDays().stream().toList(),
                contract.getWorkStartTime(),
                contract.getWorkEndTime(),
                contract.getWorkSalaryAmount(),
                contract.getWorkStartDate()
        );
    }
}
