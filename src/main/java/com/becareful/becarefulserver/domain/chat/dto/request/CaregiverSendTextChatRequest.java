package com.becareful.becarefulserver.domain.chat.dto.request;

import jakarta.validation.constraints.NotNull;

public record CaregiverSendTextChatRequest(@NotNull long chatRoomId, String text) {}
