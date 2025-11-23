package com.becareful.becarefulserver.domain.chat.dto.response;

import com.becareful.becarefulserver.domain.chat.domain.*;
import com.becareful.becarefulserver.domain.chat.domain.vo.*;
import com.becareful.becarefulserver.domain.common.domain.*;
import java.time.*;
import java.util.*;

public record ContractChatHistoryResponseDto(
        long chatId,
        ChatType chatType,
        ChatSenderType senderType,
        String sentTime,
        EnumSet<CareType> careTypes,
        EnumSet<DayOfWeek> workDays,
        LocalTime workStartTime,
        LocalTime workEndTime,
        Integer workSalaryAmount,
        LocalDate workStartDate)
        implements ChatHistoryResponseDto {
    public static ContractChatHistoryResponseDto from(Contract contract, String formattedTimeAgo) {
        return new ContractChatHistoryResponseDto(
                contract.getId(),
                ChatType.CONTRACT,
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
