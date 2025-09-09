package com.becareful.becarefulserver.domain.chat.service;

import com.becareful.becarefulserver.domain.caregiver.domain.*;
import com.becareful.becarefulserver.domain.chat.domain.*;
import com.becareful.becarefulserver.domain.chat.dto.request.*;
import com.becareful.becarefulserver.domain.chat.dto.response.*;
import com.becareful.becarefulserver.domain.chat.repository.*;
import com.becareful.becarefulserver.domain.matching.domain.*;
import com.becareful.becarefulserver.domain.matching.repository.*;
import com.becareful.becarefulserver.domain.nursing_institution.domain.*;
import com.becareful.becarefulserver.domain.socialworker.domain.*;
import com.becareful.becarefulserver.domain.socialworker.repository.*;
import static com.becareful.becarefulserver.global.exception.ErrorMessage.*;
import com.becareful.becarefulserver.global.exception.exception.*;
import com.becareful.becarefulserver.global.util.*;
import java.time.*;
import java.util.*;
import lombok.*;
import org.springframework.stereotype.*;
import org.springframework.transaction.annotation.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SocialWorkerChatService {

    private final AuthUtil authUtil;
    private final ContractRepository contractRepository;
    private final MatchingRepository matchingRepository;
    private final CompletedMatchingRepository completedMatchingRepository;
    private final SocialWorkerRepository socialWorkerRepository;
    private final SocialWorkerChatReadStatusRepository chatReadStatusRepository;
    private final SocialWorkerChatReadStatusRepository socialWorkerChatReadStatusRepository;

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

    @Transactional
    public ChatroomContentResponse getChatRoomDetailData(Long matchingId) {
        SocialWorker socialWorker = authUtil.getLoggedInSocialWorker();
        Matching matching =
                matchingRepository.findById(matchingId).orElseThrow(() -> new MatchingException(MATCHING_NOT_EXISTS));

        List<Contract> contracts = contractRepository.findByMatchingIdOrderByCreateDateAsc(matchingId);

        Contract contract = contracts.get(0);
        String caregiverName = contract.getCaregiverName();
        Integer caregiverAge = Period.between(contract.getCaregiverBirthDate(), LocalDate.now()).getYears();
        String caregiverPhoneNumber = contract.getCaregiverPhoneNumber();

        updateReadStatus(socialWorker, matching);

        return ChatroomContentResponse.of(matching, caregiverName, caregiverAge, caregiverPhoneNumber, contracts);
    }

    // 직전 계약서 내용 불러오기
    public ContractDetailResponse getContractDetail(Long contractId) {
        Contract contract =
                contractRepository.findById(contractId).orElseThrow(() -> new ContractException(CONTRACT_NOT_EXISTS));

        return ContractDetailResponse.from(contract);
    }

    @Transactional
    public Long editContract(ContractEditRequest request) {
        Matching matching = matchingRepository
                .findById(request.matchingId())
                .orElseThrow(() -> new MatchingException(MATCHING_NOT_EXISTS));
        WorkApplication workApplication = matching.getWorkApplication();
        if(workApplication == null) {
            throw new ContractException(CONTRACT_CAREGIVER_NOT_EXISTS);
        }
        Caregiver caregiver = workApplication.getCaregiver();
        Contract contract = Contract.edit(
                matching,
                caregiver,
                EnumSet.copyOf(request.workDays()),
                request.workStartTime(),
                request.workEndTime(),
                request.workSalaryUnitType(),
                request.workSalaryAmount(),
                request.workStartDate(),
                EnumSet.copyOf(request.careTypes()));

        return contractRepository.save(contract).getId();
    }

    // TODO(계약서 조율하기 채팅 엔티티 추가시 코드 수정)
    public boolean checkNewChat() {
        SocialWorker loggedInSocialWorker = authUtil.getLoggedInSocialWorker();
        return socialWorkerChatReadStatusRepository.existsUnreadContract(loggedInSocialWorker);
    }

    @Transactional
    public void updateReadStatus(SocialWorker socialWorker, Matching matching) {
        SocialWorkerChatReadStatus readStatus = chatReadStatusRepository
                .findBySocialWorkerAndMatching(socialWorker, matching)
                .orElseThrow(() -> new ChatException(SOCIAL_WORKER_CHAT_READ_STATUS_NOT_EXISTS));

        readStatus.updateLastReadAt();
    }
}
