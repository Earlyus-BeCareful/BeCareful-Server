package com.becareful.becarefulserver.domain.chat.dto.request;

import com.becareful.becarefulserver.domain.caregiver.domain.WorkSalaryUnitType;
import com.becareful.becarefulserver.domain.chat.domain.vo.ChatSendRequestType;
import com.becareful.becarefulserver.domain.chat.domain.vo.ChatSenderType;
import com.becareful.becarefulserver.domain.common.domain.CareType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public record ContractChatEditRequest(
        ChatSendRequestType sendRequestType,
        @Size(min = 1) List<DayOfWeek> workDays,
        @NotNull LocalTime workStartTime,
        @NotNull LocalTime workEndTime,
        @NotNull WorkSalaryUnitType workSalaryUnitType,
        @NotNull int workSalaryAmount,
        @NotNull LocalDate workStartDate,
        @Size(min = 1) List<CareType> careTypes
) implements  ChatSendRequest{}
