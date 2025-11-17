package com.becareful.becarefulserver.fixture;

import com.becareful.becarefulserver.domain.caregiver.domain.Caregiver;
import com.becareful.becarefulserver.domain.chat.domain.CaregiverChatReadStatus;
import com.becareful.becarefulserver.domain.chat.domain.ChatRoom;

public class CaregiverChatReadStatusFixture {
    public static CaregiverChatReadStatus getCaregiverChatReadStatus(Caregiver caregiver, ChatRoom chatRoom) {
        return CaregiverChatReadStatus.create(caregiver, chatRoom);
    }
}
