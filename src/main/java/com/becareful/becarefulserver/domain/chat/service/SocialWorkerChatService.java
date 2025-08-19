package com.becareful.becarefulserver.domain.chat.service;

import static com.becareful.becarefulserver.global.exception.ErrorMessage.CONTRACT_NOT_EXISTS;
import static com.becareful.becarefulserver.global.exception.ErrorMessage.MATCHING_NOT_EXISTS;

import com.becareful.becarefulserver.domain.chat.dto.request.ContractEditRequest;
import com.becareful.becarefulserver.domain.chat.dto.response.ChatroomContentResponse;
import com.becareful.becarefulserver.domain.chat.dto.response.ContractDetailResponse;
import com.becareful.becarefulserver.domain.chat.dto.response.SocialWorkerChatroomResponse;
import com.becareful.becarefulserver.domain.matching.domain.Contract;
import com.becareful.becarefulserver.domain.matching.domain.Matching;
import com.becareful.becarefulserver.domain.matching.repository.CompletedMatchingRepository;
import com.becareful.becarefulserver.domain.matching.repository.ContractRepository;
import com.becareful.becarefulserver.domain.matching.repository.MatchingRepository;
import com.becareful.becarefulserver.domain.nursing_institution.domain.NursingInstitution;
import com.becareful.becarefulserver.domain.socialworker.domain.SocialWorker;
import com.becareful.becarefulserver.global.exception.exception.ContractException;
import com.becareful.becarefulserver.global.exception.exception.MatchingException;
import com.becareful.becarefulserver.global.util.AuthUtil;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SocialWorkerChatService {

    private final AuthUtil authUtil;
    private final ContractRepository contractRepository;
    private final MatchingRepository matchingRepository;
    private final CompletedMatchingRepository completedMatchingRepository;

    public List<SocialWorkerChatroomResponse> getChatList() {
        SocialWorker socialworker = authUtil.getLoggedInSocialWorker();
        NursingInstitution nursingInstitution = socialworker.getNursingInstitution();
        List<Matching> matchingList = matchingRepository.findAllByNursingInstitution(nursingInstitution);

        List<SocialWorkerChatroomResponse> responses = new ArrayList<>();
        matchingList.forEach(matching -> {
            contractRepository.findTop1ByMatchingOrderByCreateDateDesc(matching).ifPresent(contract -> {
                boolean isCompleted = completedMatchingRepository.existsCompletedMatchingByContract(contract);
                var response = SocialWorkerChatroomResponse.of(matching, contract, isCompleted);
                responses.add(response);
            });
        });

        return responses;
    }

    public ChatroomContentResponse getChatRoomDetailData(Long matchingId) {
        SocialWorker socialWorker = authUtil.getLoggedInSocialWorker();

        List<Contract> contracts = contractRepository.findByMatchingIdOrderByCreateDateAsc(matchingId);
        Matching matching =
                matchingRepository.findById(matchingId).orElseThrow(() -> new MatchingException(MATCHING_NOT_EXISTS));

        matching.validateSocialWorker(socialWorker.getId());

        return ChatroomContentResponse.of(matching, contracts);
    }

    // 직전 계약서 내용 불러오기
    public ContractDetailResponse getContractDetail(Long contractId) {
        Contract contract =
                contractRepository.findById(contractId).orElseThrow(() -> new ContractException(CONTRACT_NOT_EXISTS));

        return ContractDetailResponse.from(
                contract.getMatching().getRecruitment().getElderly(),
                contract.getWorkDays().stream().toList(),
                contract.getWorkStartTime(),
                contract.getWorkEndTime(),
                contract.getWorkSalaryUnitType(),
                contract.getWorkSalaryAmount(),
                contract.getWorkStartDate());
    }

    @Transactional
    public void editContract(ContractEditRequest request) {
        Matching matching = matchingRepository
                .findById(request.matchingId())
                .orElseThrow(() -> new MatchingException(MATCHING_NOT_EXISTS));

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
}
