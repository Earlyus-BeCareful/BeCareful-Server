package com.becareful.becarefulserver.domain.chat.controller;

import com.becareful.becarefulserver.domain.chat.domain.vo.ChatSenderType;
import com.becareful.becarefulserver.domain.chat.dto.request.ChatSendRequest;

import com.becareful.becarefulserver.domain.chat.service.CaregiverChatService;
import com.becareful.becarefulserver.domain.chat.service.SocialWorkerChatService;
import com.becareful.becarefulserver.global.websocket.ChatPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@Controller
@RequiredArgsConstructor
public class ChatWebSocketController {
    private final SocialWorkerChatService socialWorkerChatService;
    private final CaregiverChatService caregiverChatService;

    @MessageMapping("/chat/send/{chatRoomId}")
    public void handleMessage(@DestinationVariable Long chatRoomId, ChatSendRequest chatSendRequest, Principal  principal) {
        ChatPrincipal p = (ChatPrincipal) principal;
        ChatSenderType senderType = p.senderType();

        switch(chatSendRequest.sendRequestType()){
            case SEND_TEXT ->
            {
                if(senderType == ChatSenderType.SOCIAL_WORKER){
                    socialWorkerChatService.sendTextChat(chatSendRequest);
                }
                    caregiverChatService.sendTextChat(chatSendRequest);
            }
            case EDIT_CONTRACT ->
            {
                if(senderType != ChatSenderType.SOCIAL_WORKER){
                    //TODO: 예외처리
                }
                socialWorkerChatService.editContractChat(chatSendRequest);
            }
            case ACCEPT_CONTRACT ->
            {
                if(senderType != ChatSenderType.CAREGIVER) {
                    //TODO: 예외처리
                }
                caregiverChatService.acceptContractChat(chatSendRequest);
            }
            case CONFIRM_MATCHING ->
            {
                if(senderType != ChatSenderType.SOCIAL_WORKER){
                    //TODO: 예외처리
                }
                socialWorkerChatService.confirmContractChat(chatSendRequest);
            }

        }
        default -> throw new IllegalArgumentException("Invalid chat send request"); //TODO: 예외 처리 수정
    }
}
