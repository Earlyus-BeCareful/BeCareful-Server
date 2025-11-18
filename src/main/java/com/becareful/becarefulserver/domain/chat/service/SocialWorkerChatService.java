package com.becareful.becarefulserver.domain.chat.service;

import static com.becareful.becarefulserver.global.exception.ErrorMessage.*;

import com.becareful.becarefulserver.domain.caregiver.domain.*;
import com.becareful.becarefulserver.domain.chat.domain.*;
import com.becareful.becarefulserver.domain.chat.domain.vo.*;
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
    private final CaregiverChatReadStatusRepository caregiverChatReadStatusRepository;
    private final ChatRepository chatRepository;

    @Transactional(readOnly = true)
    public List<SocialWorkerChatRoomSummaryResponse> getChatList() {
        SocialWorker socialWorker = authUtil.getLoggedInSocialWorker();
        List<SocialWorkerChatReadStatus> readStatusList =
                socialWorkerChatReadStatusRepository.findAllBySocialWorker(socialWorker);

        List<SocialWorkerChatRoomSummaryResponse> responses = new ArrayList<>();

        for (SocialWorkerChatReadStatus readStatus : readStatusList) {

            ChatRoom chatRoom = readStatus.getChatRoom();
            Caregiver caregiver = caregiverChatReadStatusRepository
                    .findByChatRoom(chatRoom)
                    .orElseThrow(
                            // TODO: 예외처리
                            )
                    .getCaregiver();

            Recruitment recruitment = chatRoom.getRecruitment();
            Elderly elderly = recruitment.getElderly();

            // JOIN FETCH 로 내용까지 한 번에 조회
            Chat lastChat =
                    chatRepository.findLastChatWithContent(chatRoom.getId()).orElseThrow();

            String lastChatSendTime = formatTimeAgo(lastChat.getCreateDate());

            // JOINED 구조 기반 메시지 내용 결정
            String textOfLastChat = chatRoom.getChatRoomActiveStatus() != ChatRoomActiveStatus.채팅가능
                    ? "종료된 대화입니다."
                    : resolveLastChatTextUsingInheritance(lastChat, chatRoom);

            int unreadCnt = chatRepository.countByChatRoomAndCreateDateAfter(chatRoom, readStatus.getLastReadAt());

            boolean isContractAccepted = chatRoom.getChatRoomContractStatus().equals(ChatRoomContractStatus.근무조건동의);

            responses.add(SocialWorkerChatRoomSummaryResponse.of(
                    chatRoom, caregiver, elderly, textOfLastChat, lastChatSendTime, unreadCnt, isContractAccepted));
        }
        return responses;
    }

    private String resolveLastChatTextUsingInheritance(Chat lastChat, ChatRoom chatRoom) {
        if (lastChat instanceof TextChat textChat) {
            return textChat.getText();
        }

        if (lastChat instanceof Contract) {
            int totalChatCnt = chatRepository.countByChatRoom(chatRoom);
            return getContractMessage(chatRoom.getChatRoomContractStatus(), totalChatCnt);
        }

        return "지원하지 않는 메시지 타입입니다.";
    }

    private String getContractMessage(ChatRoomContractStatus status, int totalChatCnt) {
        return switch (status) {
            case 근무조건조율중 -> (totalChatCnt == 1) ? "기관에서 근무제안이 왔습니다." : "기관에서 근무 조건을 수정했습니다.";
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
    public SocialWorkerChatRoomDetailResponse getChatRoomDetailData(Long chatRoomId) {
        SocialWorker socialWorker = authUtil.getLoggedInSocialWorker();
        SocialWorkerChatReadStatus chatReadStatus =
                socialWorkerChatReadStatusRepository.findByChatRoomIdAndSocialWorkerId(
                        chatRoomId, socialWorker.getId());

        ChatRoom chatRoom = chatRoomRepository
                .findById(chatRoomId)
                .orElseThrow(
                        // TODO: 예외처리
                        // "채팅방이 존재하지 않습니다."
                        );

        Recruitment recruitment = chatRoom.getRecruitment();

        updateReadStatus(chatReadStatus);

        Elderly elderly = recruitment.getElderly();

        Caregiver caregiver = caregiverChatReadStatusRepository
                .findByChatRoomId(chatRoomId)
                .orElseThrow(
                        // 예외처리
                        )
                .getCaregiver();

        List<Chat> chatList = chatRepository.findAllChatWithContent(chatRoomId);
        List<ChatResponseDto> chatResponseDtoList = chatList.stream()
                .map(chat -> {
                    if (chat instanceof TextChat textChat) {
                        return TextChatResponseDto.from(textChat, formatTimeAgo(textChat.getCreateDate()));
                    } else if (chat instanceof Contract contract) {
                        return (ChatResponseDto)
                                ContractChatResponseDto.from(contract, formatTimeAgo(contract.getCreateDate()));
                    } else {
                        // TODO: 예외처리
                        // "허용되지 않는 메시지 타입입니다."
                        throw new IllegalStateException();
                    }
                })
                .toList();

        return SocialWorkerChatRoomDetailResponse.of(recruitment, caregiver, elderly, chatRoom, chatResponseDtoList);
    }

    // 직전 계약서 내용 불러오기
    @Transactional(readOnly = true)
    public ContractDetailResponse getContractDetail(Long contractId) {
        Contract contract =
                contractRepository.findById(contractId).orElseThrow(() -> new ContractException(CONTRACT_NOT_EXISTS));

        return ContractDetailResponse.from(contract);
    }

    @Transactional
    public Long editContract(ContractEditRequest request) {
        ChatRoom chatRoom = chatRoomRepository
                .findById(request.chatRoomId())
                .orElseThrow(
                        // TODO: 예외처리
                        // "채팅방이 존재하지 않습니다."
                        );

        Contract contract = Contract.edit(
                chatRoom,
                EnumSet.copyOf(request.workDays()),
                request.workStartTime(),
                request.workEndTime(),
                request.workStartDate(),
                request.workSalaryUnitType(),
                request.workSalaryAmount(),
                EnumSet.copyOf(request.careTypes()));

        return contractRepository.save(contract).getId();
    }

    @Transactional
    public void createCompletedMatching(ConfirmContractRequest request) {
        SocialWorker loggedInSocialWorker = authUtil.getLoggedInSocialWorker();

        // TODO: 채팅방이 이 사회복지사가 접근 가능한 채팅방이 맞는지 검증 필요
        ChatRoom chatRoom = chatRoomRepository
                .findById(request.chatRoomId())
                .orElseThrow(
                        // TODO: 채팅방 에러처리
                        );

        checkChatRoomIsActive(chatRoom);

        Recruitment recruitment = chatRoom.getRecruitment();

        // TODO: 삭제 예정된 코드. contract 불필요.
        Contract contract = contractRepository
                .findDistinctTopByChatRoomIdOrderByCreateDateDesc(request.chatRoomId())
                .orElseThrow(() -> new ContractException(CONTRACT_NOT_EXISTS));

        Caregiver caregiver = caregiverChatReadStatusRepository
                .findByChatRoom(chatRoom)
                .orElseThrow(
                        // TODO: 예외처리
                        )
                .getCaregiver();

        Matching winnerMatching = matchingRepository
                .findByCaregiverIdAndRecruitmentId(caregiver.getId(), recruitment.getId())
                .orElseThrow(
                        // TODO: 예외처리
                        );

        List<Matching> matchings =
                matchingRepository.findAllByMatchingStatusAndRecruitment(MatchingStatus.근무제안, recruitment);

        // 나머지 매칭 실패 처리
        for (Matching m : matchings) {
            if (m.getId().equals(winnerMatching.getId())) continue;

            m.failedConfirm();
        }

        List<ChatRoom> chatRooms =
                chatRoomRepository.findAllByChatRoomActiveStatusAndRecruitment(ChatRoomActiveStatus.채팅가능, recruitment);

        for (ChatRoom room : chatRooms) {
            if (room.getId().equals(chatRoom.getId())) continue;

            room.otherMatchingConfirmed();
        }

        winnerMatching.confirm();

        // TODO: 매칭완료 생성메서드에서 contract 파라미터 삭제
        CompletedMatching completedMatching = new CompletedMatching(caregiver, contract, recruitment);
        completedMatchingRepository.save(completedMatching);
    }

    @Transactional(readOnly = true)
    public boolean checkNewChat() {
        SocialWorker loggedInSocialWorker = authUtil.getLoggedInSocialWorker();
        return socialWorkerChatReadStatusRepository.existsUnreadChat(loggedInSocialWorker.getId());
    }

    @Transactional
    public void updateReadStatus(SocialWorkerChatReadStatus chatReadStatus) {
        chatReadStatus.updateLastReadAt();
    }

    @Transactional
    public void createTextChat(SocialWorkerSendTextChatRequest request) {
        ChatRoom chatRoom = chatRoomRepository
                .findById(request.chatRoomId())
                .orElseThrow(
                        // TODO: 채팅방 존재하지 않을 경우 에러메시지 반환
                        );

        checkChatRoomIsActive(chatRoom);

        TextChat textChat = TextChat.create(chatRoom, ChatSenderType.SOCIAL_WORKER, request.text());
        chatRepository.save(textChat);
    }

    // 채팅방 검증 메서드
    private void checkChatRoomIsActive(ChatRoom chatRoom) {
        if (chatRoom.getChatRoomActiveStatus() != ChatRoomActiveStatus.채팅가능) {
            // TODO: 에러 메시지 반환
            // "채팅방이 활성화되어있지 않아, 채팅을 전송할 수 없습니다."
        }
    }
}
