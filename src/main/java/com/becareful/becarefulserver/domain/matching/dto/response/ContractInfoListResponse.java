package com.becareful.becarefulserver.domain.matching.dto.response;

import com.becareful.becarefulserver.domain.common.domain.CareType;
import com.becareful.becarefulserver.domain.matching.domain.Contract;
import com.becareful.becarefulserver.domain.matching.domain.Matching;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public record ContractInfoListResponse(
        String elderlyName,
        String nursingInstitutionName,
        String careGiverName,
        List<ContractInfoResponse> contractInfoResponseList) {

    public static ContractInfoListResponse of(Matching matching, List<Contract> contractList) {
        return new ContractInfoListResponse(
                matching.getRecruitment().getElderly().getName(),
                matching.getRecruitment().getElderly().getNursingInstitution().getName(),
                matching.getWorkApplication().getCaregiver().getName(),
                contractList.stream()
                        .map(ContractInfoListResponse.ContractInfoResponse::from)
                        .toList());
    }

    public record ContractInfoResponse(
            Long contractId,
            List<CareType> careTypes,
            List<DayOfWeek> workDays,
            LocalTime workStartTime,
            LocalTime workEndTime,
            Integer workSalaryAmount,
            LocalDate workStartDate) {
        public static ContractInfoResponse from(Contract contract) {
            return new ContractInfoListResponse.ContractInfoResponse(
                    contract.getId(),
                    contract.getCareTypes().stream().toList(),
                    contract.getWorkDays().stream().toList(),
                    contract.getWorkStartTime(),
                    contract.getWorkEndTime(),
                    contract.getWorkSalaryAmount(),
                    contract.getWorkStartDate());
        }
    }
}
