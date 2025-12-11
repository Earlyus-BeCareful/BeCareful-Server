package com.becareful.becarefulserver.domain.chat.domain;

import com.becareful.becarefulserver.domain.caregiver.domain.*;
import com.becareful.becarefulserver.domain.caregiver.domain.converter.*;
import com.becareful.becarefulserver.domain.chat.domain.vo.*;
import com.becareful.becarefulserver.domain.common.domain.*;
import com.becareful.becarefulserver.domain.matching.domain.*;
import com.becareful.becarefulserver.domain.socialworker.domain.Elderly;
import jakarta.persistence.*;
import java.time.*;
import java.util.*;
import lombok.*;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DiscriminatorValue("CONTRACT")
@PrimaryKeyJoinColumn(name = "chat_id")
public class Contract extends Chat {
    private String elderlyName;
    private int elderlyAge;

    @Enumerated(EnumType.STRING)
    private Gender elderlyGender;

    private String caregiverName;
    private String caregiverPhoneNumber;

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
            ChatSenderType chatSenderType,
            String elderlyName,
            int elderlyAge,
            Gender elderlyGender,
            String caregiverName,
            String caregiverPhoneNumber,
            EnumSet<DayOfWeek> workDays,
            LocalTime workStartTime,
            LocalTime workEndTime,
            LocalDate workStartDate,
            WorkSalaryUnitType workSalaryUnitType,
            int workSalaryAmount,
            EnumSet<CareType> careTypes) {
        super(chatRoom, chatSenderType);
        this.elderlyName = elderlyName;
        this.elderlyAge = elderlyAge;
        this.elderlyGender = elderlyGender;
        this.caregiverName = caregiverName;
        this.caregiverPhoneNumber = caregiverPhoneNumber;
        this.workDays = workDays;
        this.workStartTime = workStartTime;
        this.workEndTime = workEndTime;
        this.workStartDate = workStartDate;
        this.workSalaryUnitType = workSalaryUnitType;
        this.workSalaryAmount = workSalaryAmount;
        this.careTypes = careTypes;
    }

    public static Contract create(
            ChatRoom chatRoom, Elderly elderly, Caregiver caregiver, Recruitment recruitment, LocalDate workStartDate) {
        return Contract.builder()
                .chatRoom(chatRoom)
                .chatSenderType(ChatSenderType.SOCIAL_WORKER)
                .elderlyName(elderly.getName())
                .elderlyAge(elderly.getAge())
                .elderlyGender(elderly.getGender())
                .caregiverName(caregiver.getName())
                .caregiverPhoneNumber(caregiver.getPhoneNumber())
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
            Elderly elderly,
            Caregiver caregiver,
            EnumSet<DayOfWeek> workDays,
            LocalTime workStartTime,
            LocalTime workEndTime,
            LocalDate workStartDate,
            WorkSalaryUnitType workSalaryUnitType,
            int workSalaryAmount,
            EnumSet<CareType> careTypes) {
        return Contract.builder()
                .chatRoom(chatRoom)
                .chatSenderType(ChatSenderType.SOCIAL_WORKER)
                .elderlyName(elderly.getName())
                .elderlyAge(elderly.getAge())
                .elderlyGender(elderly.getGender())
                .caregiverName(caregiver.getName())
                .caregiverPhoneNumber(caregiver.getPhoneNumber())
                .workDays(workDays)
                .workStartTime(workStartTime)
                .workEndTime(workEndTime)
                .workStartDate(workStartDate)
                .workSalaryUnitType(workSalaryUnitType)
                .workSalaryAmount(workSalaryAmount)
                .careTypes(careTypes)
                .build();
    }

    public static Contract accept(ChatRoom chatRoom, Contract contract) {
        return Contract.builder()
                .chatRoom(chatRoom)
                .chatSenderType(ChatSenderType.CAREGIVER)
                .elderlyName(contract.getElderlyName())
                .elderlyAge(contract.getElderlyAge())
                .elderlyGender(contract.getElderlyGender())
                .caregiverName(contract.getCaregiverName())
                .caregiverPhoneNumber(contract.getCaregiverPhoneNumber())
                .workDays(contract.getWorkDays())
                .workStartTime(contract.getWorkStartTime())
                .workEndTime(contract.getWorkEndTime())
                .workStartDate(contract.getWorkStartDate())
                .workSalaryUnitType(contract.getWorkSalaryUnitType())
                .workSalaryAmount(contract.getWorkSalaryAmount())
                .careTypes(contract.getCareTypes())
                .build();
    }
}
