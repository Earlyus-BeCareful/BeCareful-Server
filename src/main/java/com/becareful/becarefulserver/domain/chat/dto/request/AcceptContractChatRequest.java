package com.becareful.becarefulserver.domain.chat.dto.request;

import com.becareful.becarefulserver.domain.chat.domain.vo.ChatSendRequestType;
import com.becareful.becarefulserver.domain.chat.domain.vo.ChatSenderType;

public record AcceptContractChatRequest(
        ChatSendRequestType sendRequestType,
        long lastContractChatId
)
implements ChatSendRequest{
}
