package com.becareful.becarefulserver.domain.chat.service;

import com.becareful.becarefulserver.domain.caregiver.domain.*;
import com.becareful.becarefulserver.domain.chat.domain.*;
import com.becareful.becarefulserver.domain.chat.domain.vo.*;
import com.becareful.becarefulserver.domain.chat.dto.request.*;
import com.becareful.becarefulserver.domain.chat.dto.response.*;
import com.becareful.becarefulserver.domain.chat.repository.*;
import com.becareful.becarefulserver.domain.matching.domain.*;
import com.becareful.becarefulserver.domain.matching.repository.*;
import com.becareful.becarefulserver.domain.nursing_institution.domain.*;
import com.becareful.becarefulserver.domain.socialworker.domain.*;
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
    private final ChatRoomRepository chatRoomRepository;
    private final ChatRepository chatRepository;
    private final TextChatRepository textChatRepository;
    private final CaregiverChatReadStatusRepository caregiverChatReadStatusRepository;

    @Transactional(readOnly = true)
    public List<CaregiverChatRoomSummaryResponse> getChatRoomList() {

        Caregiver caregiver = authUtil.getLoggedInCaregiver();
        List<CaregiverChatReadStatus> readStatusList = caregiverChatReadStatusRepository.findAllByCaregiver(caregiver);

        List<CaregiverChatRoomSummaryResponse> responses = new ArrayList<>();

        for (CaregiverChatReadStatus readStatus : readStatusList) {

            ChatRoom chatRoom = readStatus.getChatRoom();

            Recruitment recruitment = chatRoom.getRecruitment();
            NursingInstitution institution = recruitment.getElderly().getNursingInstitution();

            // JOIN FETCH 로 내용까지 한 번에 조회
            Chat lastChat =
                    chatRepository.findLastChatWithContent(chatRoom.getId()).orElseThrow();

            String lastChatSendTime = formatTimeAgo(lastChat.getCreateDate());

            // JOINED 구조 기반 메시지 내용 결정
            String textOfLastChat = chatRoom.getChatRoomActiveStatus() != ChatRoomActiveStatus.채팅가능
                    ? "종료된 대화입니다."
                    : resolveLastChatTextUsingInheritance(readStatus, lastChat, chatRoom);

            int unreadCnt = chatRepository.countByChatRoomAndCreateDateAfter(chatRoom, readStatus.getLastReadAt());

            responses.add(CaregiverChatRoomSummaryResponse.of(
                    chatRoom, institution, textOfLastChat, lastChatSendTime, unreadCnt));
        }
        return responses;
    }

    private String resolveLastChatTextUsingInheritance(
            CaregiverChatReadStatus readStatus, Chat lastChat, ChatRoom chatRoom) {

        // 1) 첫 방문
        if (readStatus.getLastReadAt().equals(LocalDateTime.MIN)) {
            return "기관에서 근무 제안이 왔습니다. 대화를 시작해보세요.";
        }

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
            case 근무조건조율중 -> (totalChatCnt == 1) ? "기관에서 근무 제안이 왔습니다." : "새로운 근무 조건을 확인해주세요.";
            case 근무조건동의 -> "요양보호사님이 조건에 동의했습니다";
            case 채용완료 -> "채용이 확정되었습니다.";
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

    // 대화내용 모두 반환
    @Transactional
    public CaregiverChatRoomDetailResponse getChatRoomDetail(Long chatRoomId) {
        Caregiver caregiver = authUtil.getLoggedInCaregiver();

        ChatRoom chatRoom = chatRoomRepository
                .findById(chatRoomId)
                .orElseThrow(
                        // TODO: 예외처리
                        // 채팅방이 존재하지 않습니다.
                        );

        Recruitment recruitment = chatRoom.getRecruitment();
        Elderly elderly = recruitment.getElderly();
        NursingInstitution institution = elderly.getNursingInstitution();
        updateReadStatus(chatRoom);

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
                        throw new IllegalArgumentException();
                    }
                })
                .toList();

        return CaregiverChatRoomDetailResponse.of(recruitment, institution, elderly, chatRoom, chatResponseDtoList);
    }

    @Transactional
    protected void updateReadStatus(ChatRoom chatRoom) {
        CaregiverChatReadStatus readStatus = caregiverChatReadStatusRepository
                .findByChatRoom(chatRoom)
                .orElseThrow(
                        // TODO: 예외처리
                        );

        readStatus.updateLastReadAt();
    }

    @Transactional
    public void saveTextChat(CaregiverSendTextChatRequest request) {

        ChatRoom chatRoom = chatRoomRepository
                .findById(request.chatRoomId())
                .orElseThrow(
                        // TODO: 채팅방 존재하지 않을 경우 에러메시지 반환
                        );

        checkChatRoomIsActive(chatRoom);

        TextChat textChat = TextChat.create(chatRoom, request.text());
        chatRepository.save(textChat);
    }

    @Transactional
    public void acceptContract(AcceptContractRequest request) {
        ChatRoom chatRoom = chatRoomRepository
                .findById(request.chatRoomId())
                .orElseThrow(
                        // TODO: 예외처리
                        );

        checkChatRoomIsActive(chatRoom);

        long lastContractId = contractRepository
                .findTopByChatRoom(chatRoom)
                .orElseThrow(
                        // TODO: 예외처리
                        // "근무조건에 동의할 계약서가 없습니다."
                        )
                .getId();

        if (request.lastContractId() != lastContractId) {
            // TODO:예외처리
            // "가장 최신의 근무조건만 동의 가능합니다."
        }
    }

    // 채팅방 검증 메서드
    private void checkChatRoomIsActive(ChatRoom chatRoom) {
        if (chatRoom.getChatRoomActiveStatus() != ChatRoomActiveStatus.채팅가능) {
            // TODO: 에러 메시지 반환
            // "채팅방이 활성화되어있지 않아, 채팅을 전송할 수 없습니다."
        }
    }
}
