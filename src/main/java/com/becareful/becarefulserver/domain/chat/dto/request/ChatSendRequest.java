package com.becareful.becarefulserver.domain.chat.dto.request;

import com.becareful.becarefulserver.domain.chat.domain.vo.ChatSendRequestType;
import com.becareful.becarefulserver.domain.chat.domain.vo.ChatSenderType;

public sealed interface ChatSendRequest permits AcceptContractChatRequest, ConfirmMatchingRequest, ContractChatEditRequest, TextChatSendRequest {
    ChatSendRequestType sendRequestType();
}
