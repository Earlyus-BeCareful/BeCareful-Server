package com.becareful.becarefulserver.domain.chat.controller;

import com.becareful.becarefulserver.domain.caregiver.domain.Caregiver;
import com.becareful.becarefulserver.domain.chat.domain.vo.ChatSenderType;
import com.becareful.becarefulserver.domain.chat.dto.request.ChatSendRequest;
import com.becareful.becarefulserver.domain.chat.domain.vo.ChatSendRequestType;

import com.becareful.becarefulserver.domain.chat.service.CaregiverChatService;
import com.becareful.becarefulserver.domain.chat.service.SocialWorkerChatService;
import com.becareful.becarefulserver.domain.socialworker.domain.SocialWorker;
import com.becareful.becarefulserver.domain.socialworker.service.SocialWorkerService;
import kotlinx.serialization.Required;
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
    public void handleMessage(@DestinationVariable Long chatRoomId, ChatSendRequest chatSendRequest, Principal principal) {
        switch(chatSendRequest.sendRequestType().toString()){
            case "SEND_TEXT" ->
            {
                if(principal instanceof SocialWorker){
                    socialWorkerChatService.sendTextChat();
                }else if(principal instanceof Caregiver){
                    caregiverChatService.sendTextChat();
                }
            }
            case "EDIT_CONTRACT" ->
            {
                if(principal instanceof SocialWorker){

                }else{
                    //TODO: 예외처리
                }
            }
            case "ACCEPT_CONTRACT" ->
            {
                if(principal instanceof Caregiver){

                }else{
                    //TODO: 예외처리
                }
            }
            case "CONFIRM_CONTRACT"  ->
            {
                if(principal instanceof SocialWorker){

                }else{
                    //TODO: 예외처리
                }
            }

        }
        default -> throw new IllegalArgumentException("Invalid chat send request"); //TODO: 예외 처리 수정
    }

    messagunng

}
