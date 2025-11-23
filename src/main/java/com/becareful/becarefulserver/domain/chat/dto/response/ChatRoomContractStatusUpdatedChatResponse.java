package com.becareful.becarefulserver.domain.chat.dto.response;

import com.becareful.becarefulserver.domain.chat.domain.vo.*;

public record ChatRoomContractStatusUpdatedChatResponse(ChatReceiveType chatType, ChatRoomContractStatus status)
        implements ChatResponse {
    public static ChatRoomContractStatusUpdatedChatResponse of(ChatRoomContractStatus status) {
        return new ChatRoomContractStatusUpdatedChatResponse(ChatReceiveType.CHATROOM_CONTRACT_STATUS_UPDATED, status);
    }
}
