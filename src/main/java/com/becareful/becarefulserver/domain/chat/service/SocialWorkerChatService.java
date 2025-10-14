package com.becareful.becarefulserver.domain.chat.service;

import static com.becareful.becarefulserver.global.exception.ErrorMessage.*;

import com.becareful.becarefulserver.domain.caregiver.domain.*;
import com.becareful.becarefulserver.domain.chat.domain.*;
import com.becareful.becarefulserver.domain.chat.dto.request.*;
import com.becareful.becarefulserver.domain.chat.dto.response.*;
import com.becareful.becarefulserver.domain.chat.repository.*;
import com.becareful.becarefulserver.domain.matching.domain.*;
import com.becareful.becarefulserver.domain.matching.repository.*;
import com.becareful.becarefulserver.domain.nursing_institution.domain.*;
import com.becareful.becarefulserver.domain.socialworker.domain.*;
import com.becareful.becarefulserver.global.exception.exception.*;
import com.becareful.becarefulserver.global.util.*;
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
    private final SocialWorkerChatReadStatusRepository chatReadStatusRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final ChatMessageRepository chatMessageRepository;

    public List<SocialWorkerChatRoomResponse> getChatRoomList() {
        SocialWorker socialworker = authUtil.getLoggedInSocialWorker();
        NursingInstitution institution = socialworker.getNursingInstitution();

        List<ChatRoom> chatRooms = chatRoomRepository.findAllByInstitution(institution);

        List<SocialWorkerChatRoomResponse> responses = new ArrayList<>();
        chatRooms.forEach(room -> {
            ChatMessage recentMessage = chatMessageRepository
                    .findRecentMessageByChatRoom(room)
                    .orElseThrow(() -> new SocialWorkerException(CHAT_MESSAGE_NOT_EXISTS));

            var response = SocialWorkerChatRoomResponse.of(room, recentMessage);
            responses.add(response);
        });

        return responses;
    }

    @Transactional
    public ChatRoomDetailResponse getChatRoomData(Long chatRoomId) {
        SocialWorker socialWorker = authUtil.getLoggedInSocialWorker();
        ChatRoom chatRoom = chatRoomRepository
                .findById(chatRoomId)
                .orElseThrow(() -> new SocialWorkerException(CHAT_ROOM_NOT_EXISTS));

        List<ChatMessage> messages = chatMessageRepository.findAllByChatRoomOrderBySeqDesc(chatRoom);

        if (messages.isEmpty()) {
            throw new ChatException(CHAT_MESSAGE_NOT_EXISTS);
        }

        updateReadStatus(messages.get(0));
        return ChatRoomDetailResponse.of(chatRoom, messages);
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
        if (workApplication == null) {
            throw new ContractException(CONTRACT_CAREGIVER_NOT_EXISTS);
        }

        Contract contract = Contract.edit(
                matching,
                EnumSet.copyOf(request.workDays()),
                request.workStartTime(),
                request.workEndTime(),
                request.workSalaryUnitType(),
                request.workSalaryAmount(),
                request.workStartDate(),
                EnumSet.copyOf(request.careTypes()));

        return contractRepository.save(contract).getId();
    }

    private void updateReadStatus(ChatMessage recentMessage) {
        SocialWorkerChatReadStatus readStatus = chatReadStatusRepository
                .findByChatRoom(recentMessage.getChatRoom())
                .orElseThrow(() -> new ChatException(CAREGIVER_CHAT_READ_STATUS_NOT_EXISTS));

        readStatus.updateLastReadSeq(recentMessage.getSeq());
    }
}
