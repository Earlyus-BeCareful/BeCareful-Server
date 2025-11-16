package com.becareful.becarefulserver.domain.chat.dto.request;

import jakarta.validation.constraints.*;

public record AcceptContractRequest(
        @NotNull long chatRoomId,
        @NotNull long lastContractId
) {
}
