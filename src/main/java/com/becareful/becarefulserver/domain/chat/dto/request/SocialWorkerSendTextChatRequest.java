package com.becareful.becarefulserver.domain.chat.dto.request;

import jakarta.validation.constraints.NotNull;

public record SocialWorkerSendTextChatRequest(@NotNull long chatRoomId, String text) {}
