package com.becareful.becarefulserver.domain.chat.dto;

import com.becareful.becarefulserver.domain.common.domain.CareType;
import com.becareful.becarefulserver.domain.chat.domain.Contract;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

public record ContractDto(
        Long contractId,
        List<CareType> careTypes,
        List<DayOfWeek> workDays,
        LocalTime workStartTime,
        LocalTime workEndTime,
        Integer workSalaryAmount,
        LocalDate workStartDate,
        LocalDateTime createDate) {

    public static ContractDto from(Contract contract) {
        return new ContractDto(
                contract.getId(),
                contract.getCareTypes().stream().toList(),
                contract.getWorkDays().stream().toList(),
                contract.getWorkStartTime(),
                contract.getWorkEndTime(),
                contract.getWorkSalaryAmount(),
                contract.getWorkStartDate(),
                contract.getCreateDate());
    }
}
