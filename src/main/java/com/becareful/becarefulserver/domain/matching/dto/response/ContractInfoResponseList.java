package com.becareful.becarefulserver.domain.matching.dto.response;

import com.becareful.becarefulserver.domain.common.domain.CareType;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public record ContractInfoResponseList(
       String elderlyName,
       String nursingInstitutionName,
       String careGiverName,
       List<ContractInfoResponse> contractInfoResponseList

) {
    public record ContractInfoResponse(
            Long contractId,
            List<CareType> careTypes,
            List<DayOfWeek> workDays,
            LocalTime workStartTime,
            LocalTime workEndTime,
            Integer workSalaryAmount,
            LocalDate workStartDate

    ) {}
}
