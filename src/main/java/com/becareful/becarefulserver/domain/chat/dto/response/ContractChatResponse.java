package com.becareful.becarefulserver.domain.chat.dto.response;

import com.becareful.becarefulserver.domain.chat.domain.*;
import com.becareful.becarefulserver.domain.chat.domain.vo.*;
import com.becareful.becarefulserver.domain.common.domain.*;
import java.time.*;
import java.util.*;

public record ContractChatResponse(
        ChatType chatType,
        long chatId,
        ChatSenderType senderType,
        String sentTime,
        EnumSet<CareType> careTypes,
        EnumSet<DayOfWeek> workDays,
        LocalTime workStartTime,
        LocalTime workEndTime,
        Integer workSalaryAmount,
        LocalDate workStartDate)
        implements ChatResponse {
    public static ContractChatResponse from(Contract contract, String formattedTimeAgo) {
        return new ContractChatResponse(
                ChatType.CONTRACT,
                contract.getId(),
                contract.getSenderType(),
                formattedTimeAgo,
                contract.getCareTypes(),
                contract.getWorkDays(),
                contract.getWorkStartTime(),
                contract.getWorkEndTime(),
                contract.getWorkSalaryAmount(),
                contract.getWorkStartDate());
    }
}
