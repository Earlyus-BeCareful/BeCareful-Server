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

    public List<CaregiverChatRoomResponse> getChatRoomList() {
        Caregiver caregiver = authUtil.getLoggedInCaregiver();
        List<Matching> matchingList =
                matchingRepository.findAllByCaregiverAndApplicationStatus(caregiver, MatchingStatus.근무제안);

        List<CaregiverChatRoomResponse> responses = new ArrayList<>();
        matchingList.forEach(matching -> {
            contractRepository.findTop1ByMatchingOrderByCreateDateDesc(matching).ifPresent(contract -> {
                boolean isCompleted = completedMatchingRepository.existsCompletedMatchingByContract(contract);
                var response = CaregiverChatRoomResponse.of(matching, contract, isCompleted);
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

        Matching matching = contract.getMatching();
        matching.confirm();

        Recruitment recruitment = matching.getRecruitment();
        matchingRepository.findAllByRecruitment(recruitment).forEach(otherMatching -> {
            switch (otherMatching.getMatchingStatus()) {
                case 지원검토중 -> otherMatching.failed();
                case 근무제안 -> otherMatching.rejectContract();
            }
        });

        CompletedMatching completedMatching = new CompletedMatching(loggedInCaregiver, contract);
        completedMatchingRepository.save(completedMatching);
    }

    private void updateReadStatus(Caregiver caregiver, Matching matching) {
        CaregiverChatReadStatus readStatus = chatReadStatusRepository
                .findByCaregiverAndMatching(caregiver, matching)
                .orElseThrow(() -> new ChatException(CAREGIVER_CHAT_READ_STATUS_NOT_EXISTS));

        readStatus.updateLastReadAt();
    }
}
