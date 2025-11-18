package com.becareful.becarefulserver.domain.chat.domain;

import com.becareful.becarefulserver.domain.caregiver.domain.*;
import com.becareful.becarefulserver.domain.caregiver.domain.converter.*;
import com.becareful.becarefulserver.domain.chat.domain.vo.*;
import com.becareful.becarefulserver.domain.common.domain.*;
import com.becareful.becarefulserver.domain.matching.domain.*;
import jakarta.persistence.*;
import java.time.*;
import java.util.*;
import lombok.*;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DiscriminatorValue("CONTRACT")
public class Contract extends Chat {

    @Convert(converter = DayOfWeekSetConverter.class)
    private EnumSet<DayOfWeek> workDays;

    private LocalTime workStartTime;
    private LocalTime workEndTime;
    private WorkSalaryUnitType workSalaryUnitType;

    private int workSalaryAmount;
    private LocalDate workStartDate;

    @Convert(converter = CareTypeSetConverter.class)
    private EnumSet<CareType> careTypes;

    @Builder
    private Contract(
            ChatRoom chatRoom,
            EnumSet<DayOfWeek> workDays,
            LocalTime workStartTime,
            LocalTime workEndTime,
            LocalDate workStartDate,
            WorkSalaryUnitType workSalaryUnitType,
            int workSalaryAmount,
            EnumSet<CareType> careTypes) {
        super(chatRoom, ChatSenderType.SOCIAL_WORKER);
        this.workDays = workDays;
        this.workStartTime = workStartTime;
        this.workEndTime = workEndTime;
        this.workStartDate = workStartDate;
        this.workSalaryUnitType = workSalaryUnitType;
        this.workSalaryAmount = workSalaryAmount;
        this.careTypes = careTypes;
    }

    public static Contract create(ChatRoom chatRoom, Recruitment recruitment, LocalDate workStartDate) {
        return Contract.builder()
                .chatRoom(chatRoom)
                .workDays(recruitment.getWorkDays())
                .workStartTime(recruitment.getWorkStartTime())
                .workEndTime(recruitment.getWorkEndTime())
                .workStartDate(workStartDate)
                .workSalaryUnitType(recruitment.getWorkSalaryUnitType())
                .workSalaryAmount(recruitment.getWorkSalaryAmount())
                .careTypes(recruitment.getCareTypes())
                .build();
    }

    public static Contract edit(
            ChatRoom chatRoom,
            EnumSet<DayOfWeek> workDays,
            LocalTime workStartTime,
            LocalTime workEndTime,
            LocalDate workStartDate,
            WorkSalaryUnitType workSalaryUnitType,
            int workSalaryAmount,
            EnumSet<CareType> careTypes) {
        return Contract.builder()
                .chatRoom(chatRoom)
                .workDays(workDays)
                .workStartTime(workStartTime)
                .workEndTime(workEndTime)
                .workStartDate(workStartDate)
                .workSalaryUnitType(workSalaryUnitType)
                .workSalaryAmount(workSalaryAmount)
                .careTypes(careTypes)
                .build();
    }
}
