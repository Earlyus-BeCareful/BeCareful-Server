package com.becareful.becarefulserver.domain.chat.dto.response;

import com.becareful.becarefulserver.domain.chat.domain.*;
import com.becareful.becarefulserver.domain.chat.domain.vo.ChatSenderType;
import com.becareful.becarefulserver.domain.common.domain.*;
import java.time.*;
import java.util.*;

public record ContractChatResponseDto(
        long chatId,
        ChatSenderType senderType,
        String sentTime,
        EnumSet<CareType> careTypes,
        EnumSet<DayOfWeek> workDays,
        LocalTime workStartTime,
        LocalTime workEndTime,
        Integer workSalaryAmount,
        LocalDate workStartDate)
        implements ChatResponseDto {
    public static ContractChatResponseDto from(Contract contract, String formattedTimeAgo) {
        return new ContractChatResponseDto(
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
