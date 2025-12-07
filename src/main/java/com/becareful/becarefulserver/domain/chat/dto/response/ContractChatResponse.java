package com.becareful.becarefulserver.domain.chat.dto.response;

import com.becareful.becarefulserver.domain.chat.domain.*;
import com.becareful.becarefulserver.domain.chat.domain.vo.*;
import com.becareful.becarefulserver.domain.common.domain.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.*;
import java.util.*;

public record ContractChatResponse(
        ChatReceiveType chatType,
        long chatId,
        ChatSenderType senderType,
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm") LocalDateTime sentTime,
        EnumSet<CareType> careTypes,
        EnumSet<DayOfWeek> workDays,
        LocalTime workStartTime,
        LocalTime workEndTime,
        Integer workSalaryAmount,
        LocalDate workStartDate)
        implements ChatResponse {
    public static ContractChatResponse from(Contract contract) {
        return new ContractChatResponse(
                ChatReceiveType.CONTRACT,
                contract.getId(),
                contract.getSenderType(),
                contract.getCreateDate(),
                contract.getCareTypes(),
                contract.getWorkDays(),
                contract.getWorkStartTime(),
                contract.getWorkEndTime(),
                contract.getWorkSalaryAmount(),
                contract.getWorkStartDate());
    }
}
