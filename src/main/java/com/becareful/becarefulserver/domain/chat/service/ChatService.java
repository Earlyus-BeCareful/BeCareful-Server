package com.becareful.becarefulserver.domain.chat.service;

import com.becareful.becarefulserver.domain.caregiver.domain.Caregiver;
import com.becareful.becarefulserver.domain.chat.domain.CaregiverChatReadStatus;
import com.becareful.becarefulserver.domain.chat.domain.ChatMessage;
import com.becareful.becarefulserver.domain.chat.domain.ChatRoom;
import com.becareful.becarefulserver.domain.chat.domain.SocialWorkerChatReadStatus;
import com.becareful.becarefulserver.domain.chat.domain.service.ChatMessageDomainService;
import com.becareful.becarefulserver.domain.chat.repository.CaregiverChatReadStatusRepository;
import com.becareful.becarefulserver.domain.chat.repository.SocialWorkerChatReadStatusRepository;
import com.becareful.becarefulserver.domain.matching.domain.Contract;
import com.becareful.becarefulserver.domain.matching.domain.Matching;
import com.becareful.becarefulserver.domain.matching.repository.ContractRepository;
import com.becareful.becarefulserver.domain.socialworker.domain.SocialWorker;
import com.becareful.becarefulserver.domain.socialworker.repository.SocialWorkerRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final ContractRepository contractRepository;
    private final ChatMessageDomainService chatMessageDomainService;
    private final CaregiverChatReadStatusRepository caregiverChatReadStatusRepository;
    private final SocialWorkerRepository socialWorkerRepository;
    private final SocialWorkerChatReadStatusRepository socialWorkerChatReadStatusRepository;

    /**
     * 2025-10-14
     * Chat Service 는 다른 서비스에서 참조하는 서비스이므로,
     * 순환참조를 방지하기 위해, Chat Service 에서는 다른 Service 를 주입받지 않으며,
     * @Transactional 어노테이션도 사용하지 않습니다.
     */
    public void createChatRoom(Matching matching, SocialWorker socialWorker, Contract contract) {
        // 채팅 생성
        ChatRoom chatRoom = ChatRoom.create(matching);
        ChatMessage defaultMessage = chatMessageDomainService.createFirstMessage(socialWorker, contract, chatRoom);

        // Caregiver 상태 생성
        Caregiver caregiver = matching.getWorkApplication().getCaregiver();
        CaregiverChatReadStatus caregiverStatus = CaregiverChatReadStatus.create(caregiver, matching);
        caregiverChatReadStatusRepository.save(caregiverStatus);
        // SocialWorker 상태 생성
        List<SocialWorker> socialWorkers =
                socialWorkerRepository.findAllByNursingInstitution(socialWorker.getNursingInstitution());
        List<SocialWorkerChatReadStatus> socialWorkerChatReadStatuses = socialWorkers.stream()
                .map(s -> SocialWorkerChatReadStatus.create(s, matching))
                .toList();
        socialWorkerChatReadStatusRepository.saveAll(socialWorkerChatReadStatuses);
    }
}
