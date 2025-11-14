package com.becareful.becarefulserver.domain.chat.service;

import static com.becareful.becarefulserver.global.exception.ErrorMessage.*;

import com.becareful.becarefulserver.domain.caregiver.domain.*;
import com.becareful.becarefulserver.domain.chat.domain.*;
import com.becareful.becarefulserver.domain.chat.dto.request.ConfirmContractRequest;
import com.becareful.becarefulserver.domain.chat.domain.vo.ChatRoomStatus;
import com.becareful.becarefulserver.domain.chat.domain.vo.ChatType;
import com.becareful.becarefulserver.domain.chat.dto.request.CaregiverSendTextChatRequest;
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
    private final ChatRoomRepository chatRoomRepository;
    private final ChatRepository chatRepository;
    private final TextChatRepository textChatRepository;

    public List<CaregiverChatroomResponse> getChatList() {
        Caregiver caregiver = authUtil.getLoggedInCaregiver();
        List<Matching> matchingList =
                matchingRepository.findAllByCaregiverAndApplicationStatus(caregiver, MatchingStatus.근무제안);

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
    public ChatRoomDetailResponse getChatRoomDetail(Long chatRoomId) {
        Caregiver caregiver = authUtil.getLoggedInCaregiver();

        ChatRoom chatRoom = chatRoomRepository
                .findById(chatRoomId)
                .ifPresentOrElse(
                        ()->);//TODO(예외처리)

        Matching matching = chatRoom.getMatching();

        matching.validateCaregiver(caregiver.getId());
        updateReadStatus(chatRoom);

        List<Contract> contracts = contractRepository.findByMatchingOrderByCreateDateAsc(matching);
        return ChatRoomDetailResponse.of(matching, contracts);
    }

    @Transactional
    public void createCompletedMatching(ConfirmContractRequest request) {
        Caregiver loggedInCaregiver = authUtil.getLoggedInCaregiver();

        ChatRoom chatRoom = chatRoomRepository.findById(request.chatRoomId()).orElseThrow(
                //TODO: 채팅방 에러처리
        );

        Long lastContractId = chatRoomRepository.findLastContractIdByChatRoomId(request.chatRoomId());
        Contract contract = contractRepository.findById(lastContractId).orElseThrow(() -> new ContractException(CONTRACT_NOT_EXISTS));

        Matching matching = chatRoom.getMatching();
        matching.confirm();

        Recruitment recruitment = matching.getRecruitment();
        matchingRepository.findAllByRecruitment(recruitment).forEach(otherMatching -> {
            switch (otherMatching.getMatchingStatus()) {
                case 지원검토 -> otherMatching.failed();
                case 근무제안 -> otherMatching.rejectContract();
            }
        });

        CompletedMatching completedMatching = new CompletedMatching(loggedInCaregiver, contract);
        completedMatchingRepository.save(completedMatching);
    }

    @Transactional
    public void updateReadStatus(ChatRoom chatRoom) {
        CaregiverChatReadStatus readStatus = chatReadStatusRepository
                .findByChatRoom(chatRoom)
                .ifPresentOrElse(() -> new ChatException(CAREGIVER_CHAT_READ_STATUS_NOT_EXISTS));

        readStatus.updateLastReadAt();
    }

    //채팅방 검증 메서드
    //TODO: 유효한 차팅방인지 검증
    boolean check (ChatRoom chatRoom){
        if(chatRoom.getChatRoomStatus() != ChatRoomStatus.활성화) {
            //TODO: 에러 메시지 반환
        }
    }

    @Transactional
    public void saveTextChat(CaregiverSendTextChatRequest request) {
        TextChat textChat = TextChat.create(request.text());
        textChatRepository.save(textChat);

        ChatRoom chatRoom = chatRoomRepository.findById(request.chatRoomId()).orElseThrow(
                //TODO: 채팅방 존재하지 않을 경우 에러메시지 반환
        );

        Chat newChat = Chat.create(chatRoom, ChatType.TEXT, textChat.getId());
        chatRepository.save(newChat);
    }
}
