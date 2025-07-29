package com.becareful.becarefulserver.domain.matching.dto;

import com.becareful.becarefulserver.domain.common.domain.CareType;
import com.becareful.becarefulserver.domain.matching.domain.Recruitment;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.EnumSet;

public record SocialWorkerRecruitmentSimpleDto(
        ElderlyDto elderlyInfo,
        EnumSet<CareType> careType,
        EnumSet<DayOfWeek> workDays,
        LocalTime workStartTime,
        LocalTime workEndTime) {
    public static SocialWorkerRecruitmentSimpleDto from(Recruitment recruitment) {
        return new SocialWorkerRecruitmentSimpleDto(
                ElderlyDto.from(recruitment.getElderly()),
                recruitment.getCareTypes(),
                recruitment.getWorkDays(),
                recruitment.getWorkStartTime(),
                recruitment.getWorkEndTime());
    }
}
