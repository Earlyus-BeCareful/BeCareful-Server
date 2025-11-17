package com.becareful.becarefulserver.fixture;

import com.becareful.becarefulserver.domain.caregiver.domain.Caregiver;
import com.becareful.becarefulserver.domain.chat.domain.ChatRoom;
import com.becareful.becarefulserver.domain.matching.domain.Recruitment;

public class ChatRoomFixture {
    public static ChatRoom createChatRoom(Recruitment recruitment, Caregiver caregiver) {
        return ChatRoom.create(recruitment);
    }
}
