package com.becareful.becarefulserver.domain.chat.service;

import com.becareful.becarefulserver.domain.chat.domain.vo.*;
import static com.becareful.becarefulserver.global.exception.ErrorMessage.*;

import com.becareful.becarefulserver.domain.caregiver.domain.*;
import com.becareful.becarefulserver.domain.chat.domain.*;
import com.becareful.becarefulserver.domain.chat.dto.request.*;
import com.becareful.becarefulserver.domain.chat.dto.response.*;
import com.becareful.becarefulserver.domain.chat.repository.*;
import com.becareful.becarefulserver.domain.matching.domain.*;
import com.becareful.becarefulserver.domain.matching.repository.*;
import com.becareful.becarefulserver.domain.socialworker.domain.*;
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
    private final ChatRoomRepository chatRoomRepository;
    private final SocialWorkerChatReadStatusRepository socialWorkerChatReadStatusRepository;
    private final RecruitmentRepository recruitmentRepository;
    private final CaregiverChatReadStatusRepository caregiverChatReadStatusRepository;
    private final ChatRepository chatRepository;

    public List<SocialWorkerChatRoomSummaryResponse> getChatList() {
        SocialWorker socialWorker =  authUtil.getLoggedInSocialWorker();
        List<SocialWorkerChatReadStatus> readStatusList =
                socialWorkerChatReadStatusRepository.findAllBySocialWorker(socialWorker);

        List<SocialWorkerChatRoomSummaryResponse> responses = new ArrayList<>();

        for (SocialWorkerChatReadStatus readStatus : readStatusList) {

            ChatRoom chatRoom = readStatus.getChatRoom();
            Caregiver caregiver = caregiverChatReadStatusRepository.findByChatRoom(chatRoom).orElseThrow(
                    //TODO: 예외처리
            ).getCaregiver();

            Recruitment recruitment = chatRoom.getRecruitment();
            Elderly elderly = recruitment.getElderly();

            //JOIN FETCH 로 내용까지 한 번에 조회
            Chat lastChat = chatRepository
                    .findLastChatWithContent(chatRoom.getId())
                    .orElseThrow();

            String lastChatSendTime = formatTimeAgo(lastChat.getCreateDate());

            //JOINED 구조 기반 메시지 내용 결정
            String textOfLastChat = chatRoom.getChatRoomActivateStatus()!=ChatRoomActivateStatus.채팅가능 ?
                    "종료된 대화입니다.":
                    resolveLastChatTextUsingInheritance(lastChat, chatRoom);

            int unreadCnt = chatRepository.countByChatRoomAndCreateDateAfter(chatRoom, readStatus.getLastReadAt());

            boolean isContractAccepted = chatRoom.getChatRoomContractStatus().equals(ChatRoomContractStatus.근무조건동의);

            responses.add(
                    SocialWorkerChatRoomSummaryResponse.of(
                            chatRoom,
                            caregiver,
                            elderly,
                            textOfLastChat,
                            lastChatSendTime,
                            unreadCnt,
                            isContractAccepted
                    )
            );
        }
        return responses;
    }


    private String resolveLastChatTextUsingInheritance(
            Chat lastChat,
            ChatRoom chatRoom
    ) {

        // 2) 텍스트 메시지
        if (lastChat instanceof TextChat textChat) {
            return textChat.getText();
        }

        // 3) 계약 관련 메시지
        if (lastChat instanceof Contract) {
            int totalChatCnt = chatRepository.countByChatRoom(chatRoom);
            return getContractMessage(chatRoom.getChatRoomContractStatus(), totalChatCnt);
        }

        return "지원하지 않는 메시지 타입입니다.";
    }


    private String getContractMessage(ChatRoomContractStatus status, int totalChatCnt) {
        return switch (status) {
            case 근무조건조율중  -> (totalChatCnt == 1)
                    ? "기관에서 근무제안이 왔습니다."
                    : "기관에서 근무 조건을 수정했습니다.";
            case 근무조건동의 -> "요양보호사님이 근무조건을 동의했습니다.";
            case 채용완료 -> "최종 채용이 확정되었습니다";
            default -> null;
        };
    }


    public static String formatTimeAgo(LocalDateTime sendTime) {

        Duration duration = Duration.between(sendTime, LocalDateTime.now());

        long hours = duration.toHours();
        long minutes = duration.toMinutes();
        long days = duration.toDays();

        if (hours < 1) return minutes + "분 전";
        if (days < 1) return hours + "시간 전";
        return days + "일 전";
    }

    @Transactional
    public SocialWorkerChatRoomDetailResponse getChatRoomDetailData(Long matchingId) {
        SocialWorker socialWorker = authUtil.getLoggedInSocialWorker();
        Matching matching =
                matchingRepository.findById(matchingId).orElseThrow(() -> new MatchingException(MATCHING_NOT_EXISTS));

        updateReadStatus(socialWorker, matching);

        List<Contract> contracts = contractRepository.findByMatchingOrderByCreateDateAsc(matching);
        return SocialWorkerChatRoomDetailResponse.of(recruitment, caregiver, elderly, chatRoom, chatList);
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
                EnumSet.copyOf(request.workDays()),
                request.workStartTime(),
                request.workEndTime(),
                request.workSalaryUnitType(),
                request.workSalaryAmount(),
                request.workStartDate(),
                EnumSet.copyOf(request.careTypes()));

        return contractRepository.save(contract).getId();
    }

    @Transactional
    public void createCompletedMatching(ConfirmContractRequest request) {
        SocialWorker loggedInSocialWorker = authUtil.getLoggedInSocialWorker();

        ChatRoom chatRoom = chatRoomRepository.findById(request.chatRoomId()).orElseThrow(
                //TODO: 채팅방 에러처리
        );



        Long lastContractId = chatRoomRepository.findLastContractIdByChatRoomId(request.chatRoomId());
        Contract contract = contractRepository.findById(lastContractId).orElseThrow(() -> new ContractException(CONTRACT_NOT_EXISTS));

        Recruitment recruitment = chatRoom.getRecruitment();
        Caregiver caregiver = caregiverChatReadStatusRepository.findByChatRoom(chatRoom).orElseThrow().getCaregiver();
        Matching mathing = matchingRepository.findByWorkApplicationAndRecruitment()
        matching.confirm();

        recruitment.complete();
        matchingRepository.findAllByRecruitment(recruitment).forEach(otherMatching -> {
                ChatRoom otherChatRoom = chatRoomRepository.findByMatching(otherMatching).orElseThrow(
                        //TODO: 예외처리
                );
                otherChatRoom.updateStatusTo(ChatRoomActivateStatus.채용완료);
        });

        chatRoom.updateStatusTo(ChatRoomActivateStatus.채팅가능);

        CompletedMatching completedMatching = new CompletedMatching(matching.getWorkApplication().getCaregiver(), contract);
        completedMatchingRepository.save(completedMatching);
    }

    // TODO(계약서 조율하기 채팅 엔티티 추가시 코드 수정)
    public boolean checkNewChat() {
        SocialWorker loggedInSocialWorker = authUtil.getLoggedInSocialWorker();
        return socialWorkerChatReadStatusRepository.existsUnreadChat(loggedInSocialWorker.getId());
    }

    @Transactional
    public void updateReadStatus(SocialWorker socialWorker, Matching matching) {
        SocialWorkerChatReadStatus readStatus = socialWorkerChatReadStatusRepository
                .findBySocialWorkerAndMatching(socialWorker, matching)
                .orElseThrow(() -> new ChatException(SOCIAL_WORKER_CHAT_READ_STATUS_NOT_EXISTS));

        readStatus.updateLastReadAt();
    }
}
