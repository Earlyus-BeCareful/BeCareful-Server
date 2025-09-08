package com.becareful.becarefulserver.domain.chat.dto.response;

import com.becareful.becarefulserver.domain.caregiver.domain.WorkSalaryUnitType;
import com.becareful.becarefulserver.domain.common.domain.CareType;
import com.becareful.becarefulserver.domain.common.domain.DetailCareType;
import com.becareful.becarefulserver.domain.matching.domain.Contract;
import com.becareful.becarefulserver.domain.socialworker.domain.Elderly;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public record ContractDetailResponse(
        Long matchingId,
        List<DayOfWeek> workDays,
        LocalTime workStartTime,
        LocalTime workEndTime,
        WorkSalaryUnitType workSalaryUnitType,
        Integer workSalaryAmount,
        LocalDate workStartDate,
        List<CareInfoResponse> careInfoList) {
    public record CareInfoResponse(CareType careType, List<String> detailCareTypes, Boolean check) {}

    public static ContractDetailResponse from(Contract contract) {
        Elderly elderly = contract.getMatching().getRecruitment().getElderly();

        // CareType별로 detailCareTypes 그룹화
        Map<CareType, List<String>> groupedCareInfo = elderly.getDetailCareTypes().stream()
                .collect(Collectors.groupingBy(
                        DetailCareType::getCareType,
                        Collectors.mapping(DetailCareType::getDisplayName, Collectors.toList())));

        // CareInfoResponse 리스트 생성
        List<CareInfoResponse> careInfoList = groupedCareInfo.entrySet().stream()
                .map(entry -> new CareInfoResponse(entry.getKey(), entry.getValue(), true)) // 기본값 true
                .collect(Collectors.toList());

        return new ContractDetailResponse(
                contract.getMatching().getId(),
                contract.getWorkDays().stream().toList(),
                contract.getWorkStartTime(),
                contract.getWorkEndTime(),
                contract.getWorkSalaryUnitType(),
                contract.getWorkSalaryAmount(),
                contract.getWorkStartDate(),
                careInfoList);
    }
}
