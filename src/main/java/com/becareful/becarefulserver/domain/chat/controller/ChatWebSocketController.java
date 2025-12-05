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
            @DestinationVariable Long chatRoomId, ChatSendRequest chatSendRequest, Principal principal) {

        if (!(principal instanceof ChatPrincipal p)) {
            log.error("Principal toString: {}", principal.toString());
            throw new IllegalStateException("WebSocket principal is not ChatPrincipal");
        }

        ChatSenderType senderType = p.senderType();

        switch (chatSendRequest.sendRequestType()) {
            case SEND_TEXT -> {
                if (senderType == ChatSenderType.SOCIAL_WORKER) {
                    socialWorkerChatService.sendTextChat(chatRoomId, (SendTextChatRequest) chatSendRequest);
                } else {
                    caregiverChatService.sendTextChat(chatRoomId, (SendTextChatRequest) chatSendRequest);
                }
            }
            case EDIT_CONTRACT -> {
                if (senderType != ChatSenderType.SOCIAL_WORKER) {
                    // TODO: 예외처리
                }
                socialWorkerChatService.editContractChat(chatRoomId, (EditContractChatRequest) chatSendRequest);
            }
            case ACCEPT_CONTRACT -> {
                if (senderType != ChatSenderType.CAREGIVER) {
                    // TODO: 예외처리
                }
                caregiverChatService.acceptContractChat(chatRoomId, (AcceptContractChatRequest) chatSendRequest);
            }
            case CONFIRM_MATCHING -> {
                if (senderType != ChatSenderType.SOCIAL_WORKER) {
                    // TODO: 예외처리
                }
                socialWorkerChatService.confirmContractChat(chatRoomId, (ConfirmContractChatRequest) chatSendRequest);
            }

            default -> throw new IllegalArgumentException("Invalid chat send request"); // TODO: 예외 처리 수정
        }
    }
}
