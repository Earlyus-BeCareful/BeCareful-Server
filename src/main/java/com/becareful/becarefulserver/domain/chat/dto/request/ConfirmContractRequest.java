package com.becareful.becarefulserver.domain.chat.dto.request;

import jakarta.validation.constraints.NotNull;

public record ConfirmContractRequest(@NotNull long chatRoomId, @NotNull long lastContractId) {}
