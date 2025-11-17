package com.becareful.becarefulserver.fixture;

import com.becareful.becarefulserver.domain.chat.domain.ChatRoom;
import com.becareful.becarefulserver.domain.chat.domain.SocialWorkerChatReadStatus;
import com.becareful.becarefulserver.domain.socialworker.domain.SocialWorker;

public class SocialWorkerChatReadStatusFixture {
    public static SocialWorkerChatReadStatus create(SocialWorker socialWorker, ChatRoom chatRoom){
        return SocialWorkerChatReadStatus.create(socialWorker, chatRoom);
    }

}
