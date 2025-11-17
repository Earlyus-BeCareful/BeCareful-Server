package com.becareful.becarefulserver.domain.matching.dto.response;

import com.becareful.becarefulserver.domain.common.domain.CareType;
import com.becareful.becarefulserver.domain.matching.domain.CompletedMatching;
import com.becareful.becarefulserver.domain.matching.dto.ElderlyDto;
import com.becareful.becarefulserver.domain.socialworker.domain.Elderly;
import java.time.DayOfWeek;
import java.util.List;

public record CompletedMatchingInfoResponse(
        Long id, List<DayOfWeek> workDays, List<CareType> careTypes, String note, ElderlyDto elderlyInfo) {
    public static CompletedMatchingInfoResponse from(CompletedMatching completedMatching) {
        Elderly elderly = completedMatching.getRecruitment().getElderly();

        return new CompletedMatchingInfoResponse(
                completedMatching.getId(),
                completedMatching.getContract().getWorkDays().stream().toList(),
                completedMatching.getContract().getCareTypes().stream().toList(),
                completedMatching.getNote(),
                ElderlyDto.from(elderly));
    }
}
