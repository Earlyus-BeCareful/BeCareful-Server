package com.becareful.becarefulserver.domain.chat.controller;

import com.becareful.becarefulserver.domain.chat.domain.vo.ChatSenderType;
import com.becareful.becarefulserver.domain.chat.dto.request.*;
import com.becareful.becarefulserver.domain.chat.service.CaregiverChatService;
import com.becareful.becarefulserver.domain.chat.service.SocialWorkerChatService;
import com.becareful.becarefulserver.global.websocket.ChatPrincipal;
import java.security.Principal;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class ChatWebSocketController {
    private static final Logger log = LoggerFactory.getLogger(ChatWebSocketController.class);
    private final SocialWorkerChatService socialWorkerChatService;
    private final CaregiverChatService caregiverChatService;

    @MessageMapping("/chat/send/{chatRoomId}")
    public void handleMessage(
            @DestinationVariable Long chatRoomId, ChatSendRequest chatSendRequest, Principal principal)
            throws IllegalAccessException {
        log.info("MessageMapping 시작");

        if (!(principal instanceof ChatPrincipal p)) {
            log.error("Principal class:{}, name:{}", principal.getClass(), principal.getName());
            throw new IllegalStateException("WebSocket principal is not ChatPrincipal");
        }

        ChatSenderType senderType = p.senderType();

        switch (chatSendRequest.sendRequestType()) {
            case SEND_TEXT -> {
                if (senderType == ChatSenderType.SOCIAL_WORKER) {
                    socialWorkerChatService.sendTextChat(chatRoomId, (SendTextChatRequest) chatSendRequest);
                } else if (senderType == ChatSenderType.CAREGIVER) {
                    caregiverChatService.sendTextChat(chatRoomId, (SendTextChatRequest) chatSendRequest);
                } else throw new IllegalAccessException("텍스트 전송은 사회복지사, 요양보호사만 가능합니다.");
            }
            case EDIT_CONTRACT -> {
                if (senderType != ChatSenderType.SOCIAL_WORKER) {
                    throw new IllegalAccessException("근무조건 수정은 사회복지사만 가능합니다.");
                }
                socialWorkerChatService.editContractChat(chatRoomId, (EditContractChatRequest) chatSendRequest);
            }
            case ACCEPT_CONTRACT -> {
                if (senderType != ChatSenderType.CAREGIVER) {
                    throw new IllegalAccessException("근무조건 수락은 요양보호사만 가능합니다.");
                }
                caregiverChatService.acceptContractChat(chatRoomId, (AcceptContractChatRequest) chatSendRequest);
            }
            case CONFIRM_MATCHING -> {
                if (senderType != ChatSenderType.SOCIAL_WORKER) {
                    throw new IllegalAccessException("매칭 확정은 사회복지사만 가능합니다.");
                }
                socialWorkerChatService.confirmContractChat(chatRoomId, (ConfirmContractChatRequest) chatSendRequest);
            }

            default -> throw new IllegalArgumentException("Invalid chat send request"); // TODO: 예외 처리 수정
        }
    }
}
