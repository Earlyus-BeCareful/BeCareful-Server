package com.becareful.becarefulserver.domain.chat.dto.response;

import com.becareful.becarefulserver.domain.chat.domain.vo.*;

// TODO: 탈퇴, 공고 마감, 채옹 확정으로 인한 채팅방 활성화 상태 변경시 채팅방 웹소켓 연결된 사용자에게 알림을 보내기 위한 dto
public record ChatRoomActiveStatusUpdatedChatResponse(ChatType chatType, ChatRoomActiveStatus status)
        implements ChatResponse {
    public static ChatRoomActiveStatusUpdatedChatResponse of(ChatRoomActiveStatus status) {
        return new ChatRoomActiveStatusUpdatedChatResponse(ChatType.CHATROOM_ACTIVE_STATUS_UPDATED, status);
    }
}
