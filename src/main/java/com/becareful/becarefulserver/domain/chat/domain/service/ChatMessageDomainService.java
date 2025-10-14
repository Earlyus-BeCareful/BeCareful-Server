package com.becareful.becarefulserver.domain.chat.domain.service;

import com.becareful.becarefulserver.domain.chat.domain.ChatMessage;
import com.becareful.becarefulserver.domain.chat.domain.ChatRoom;
import com.becareful.becarefulserver.domain.matching.domain.Contract;
import com.becareful.becarefulserver.domain.socialworker.domain.SocialWorker;
import org.springframework.stereotype.Component;

@Component
public class ChatMessageDomainService {

    public ChatMessage createFirstMessage(SocialWorker socialWorker, Contract contract, ChatRoom chatRoom) {
        return ChatMessage.createSocialWorkerMessage(1L, "합격을 축하드립니다.", socialWorker, contract, chatRoom);
    }
}
