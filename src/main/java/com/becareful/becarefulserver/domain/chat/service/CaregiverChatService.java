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
public class CaregiverChatService {

    private final AuthUtil authUtil;
    private final MatchingRepository matchingRepository;
    private final ContractRepository contractRepository;
    private final CompletedMatchingRepository completedMatchingRepository;
    private final CaregiverChatReadStatusRepository chatReadStatusRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final ChatMessageRepository chatMessageRepository;

    @Transactional(readOnly = true)
    public List<CaregiverChatRoomResponse> getChatRoomList() {
        Caregiver caregiver = authUtil.getLoggedInCaregiver();

        List<ChatRoom> chatRoomList = chatRoomRepository.findAllByCaregiver(caregiver);

        List<CaregiverChatRoomResponse> responses = new ArrayList<>();
        chatRoomList.forEach(room -> {
            ChatMessage message = chatMessageRepository
                    .findRecentMessageByChatRoom(room)
                    .orElseThrow(() -> new DomainException(CHAT_MESSAGE_NOT_EXISTS));
            var response = CaregiverChatRoomResponse.of(room, message);
            responses.add(response);
        });

        return responses;
    }

    @Transactional(readOnly = true)
    public ChatRoomDetailResponse getChatRoomData(Long chatRoomId) {
        Caregiver caregiver = authUtil.getLoggedInCaregiver();

        ChatRoom chatRoom = chatRoomRepository
                .findById(chatRoomId)
                .orElseThrow(() -> new SocialWorkerException(CHAT_ROOM_NOT_EXISTS));

        updateReadStatus(caregiver, chatRoom);

        List<ChatMessage> messages = chatMessageRepository.findAllByChatRoomOrderByCreateDateDesc(chatRoom);
        return ChatRoomDetailResponse.of(chatRoom, messages);
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
