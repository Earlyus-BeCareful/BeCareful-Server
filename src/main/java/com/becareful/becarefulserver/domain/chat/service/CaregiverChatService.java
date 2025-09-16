package com.becareful.becarefulserver.domain.chat.service;

import static com.becareful.becarefulserver.global.exception.ErrorMessage.*;

import com.becareful.becarefulserver.domain.caregiver.domain.*;
import com.becareful.becarefulserver.domain.chat.domain.*;
import com.becareful.becarefulserver.domain.chat.dto.response.*;
import com.becareful.becarefulserver.domain.chat.repository.*;
import com.becareful.becarefulserver.domain.matching.domain.*;
import com.becareful.becarefulserver.domain.matching.repository.*;
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
public class CaregiverChatService {

    private final AuthUtil authUtil;
    private final MatchingRepository matchingRepository;
    private final ContractRepository contractRepository;
    private final CompletedMatchingRepository completedMatchingRepository;
    private final CaregiverChatReadStatusRepository chatReadStatusRepository;

    public List<CaregiverChatroomResponse> getChatList() {
        Caregiver caregiver = authUtil.getLoggedInCaregiver();
        List<Matching> matchingList =
                matchingRepository.findAllByCaregiverAndApplicationStatus(caregiver, MatchingApplicationStatus.근무제안);

        List<CaregiverChatroomResponse> responses = new ArrayList<>();
        matchingList.forEach(matching -> {
            contractRepository.findTop1ByMatchingOrderByCreateDateDesc(matching).ifPresent(contract -> {
                boolean isCompleted = completedMatchingRepository.existsCompletedMatchingByContract(contract);
                var response = CaregiverChatroomResponse.of(matching, contract, isCompleted);
                responses.add(response);
            });
        });

        return responses;
    }

    @Transactional
    public ChatRoomDetailResponse getChatRoomDetail(Long matchingId) {
        Caregiver caregiver = authUtil.getLoggedInCaregiver();

        Matching matching =
                matchingRepository.findById(matchingId).orElseThrow(() -> new MatchingException(MATCHING_NOT_EXISTS));
        matching.validateCaregiver(caregiver.getId());

        updateReadStatus(caregiver, matching);

        List<Contract> contracts = contractRepository.findByMatchingOrderByCreateDateAsc(matching);
        return ChatRoomDetailResponse.of(matching, contracts);
    }

    @Transactional
    public void createCompletedMatching(Long contractId) {
        Caregiver loggedInCaregiver = authUtil.getLoggedInCaregiver();
        Contract contract =
                contractRepository.findById(contractId).orElseThrow(() -> new ContractException(CONTRACT_NOT_EXISTS));

        CompletedMatching completedMatching = new CompletedMatching(loggedInCaregiver, contract);
        completedMatchingRepository.save(completedMatching);
    }

    // TODO(계약서 조율하기 채팅 엔티티 추가시 코드 수정)
    public boolean checkNewChat(Caregiver caregiver) {
        return chatReadStatusRepository.existsUnreadContract(caregiver);
    }

    @Transactional
    public void updateReadStatus(Caregiver caregiver, Matching matching) {
        CaregiverChatReadStatus readStatus = chatReadStatusRepository
                .findByCaregiverAndMatching(caregiver, matching)
                .orElseThrow(() -> new ChatException(CAREGIVER_CHAT_READ_STATUS_NOT_EXISTS));

        readStatus.updateLastReadAt();
    }
}
