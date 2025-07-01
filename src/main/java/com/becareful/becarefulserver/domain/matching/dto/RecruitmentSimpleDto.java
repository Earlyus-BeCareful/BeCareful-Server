package com.becareful.becarefulserver.domain.matching.dto;

import com.becareful.becarefulserver.domain.common.domain.CareType;
import com.becareful.becarefulserver.domain.common.vo.Gender;
import com.becareful.becarefulserver.domain.matching.domain.Recruitment;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.EnumSet;

public record RecruitmentSimpleDto(
        String elderlyName,
        EnumSet<CareType> careType,
        int elderlyAge,
        Gender gender,
        EnumSet<DayOfWeek> workDays,
        LocalTime workStartTime,
        LocalTime workEndTime) {
    public static RecruitmentSimpleDto from(Recruitment recruitment) {
        return new RecruitmentSimpleDto(
                recruitment.getElderly().getName(),
                recruitment.getCareTypes(),
                recruitment.getElderly().getAge(),
                recruitment.getElderly().getGender(),
                recruitment.getWorkDays(),
                recruitment.getWorkStartTime(),
                recruitment.getWorkEndTime());
    }
}
