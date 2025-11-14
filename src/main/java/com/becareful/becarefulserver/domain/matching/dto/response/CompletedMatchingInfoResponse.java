package com.becareful.becarefulserver.domain.matching.dto.response;

import com.becareful.becarefulserver.domain.common.domain.CareType;
import com.becareful.becarefulserver.domain.matching.domain.CompletedMatching;
import com.becareful.becarefulserver.domain.matching.domain.Contract;
import com.becareful.becarefulserver.domain.matching.dto.ElderlyDto;
import com.becareful.becarefulserver.domain.socialworker.domain.Elderly;
import java.time.DayOfWeek;
import java.time.format.DateTimeFormatter;
import java.util.List;

public record CompletedMatchingInfoResponse(
        Long id,
        List<DayOfWeek> workDays,
        String workStartTime,
        String workEndTime,
        List<CareType> careTypes,
        String note,
        ElderlyDto elderlyInfo) {
    public static CompletedMatchingInfoResponse from(CompletedMatching completedMatching) {
        Contract contract = completedMatching.getContract();
        Elderly elderly = contract.getMatching().getRecruitment().getElderly();
        return new CompletedMatchingInfoResponse(
                completedMatching.getId(),
                contract.getWorkDays().stream().toList(),
                contract.getWorkStartTime().format(DateTimeFormatter.ofPattern("HH:mm")),
                contract.getWorkEndTime().format(DateTimeFormatter.ofPattern("HH:mm")),
                contract.getCareTypes().stream().toList(),
                completedMatching.getNote(),
                ElderlyDto.from(elderly));
    }
}
