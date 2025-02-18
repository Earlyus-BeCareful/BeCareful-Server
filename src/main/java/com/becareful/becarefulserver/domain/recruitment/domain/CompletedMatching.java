package com.becareful.becarefulserver.domain.recruitment.domain;

import com.becareful.becarefulserver.domain.caregiver.domain.Caregiver;
import com.becareful.becarefulserver.domain.caregiver.domain.WorkApplication;
import com.becareful.becarefulserver.domain.caregiver.domain.WorkSalaryType;
import com.becareful.becarefulserver.domain.caregiver.domain.WorkTime;
import com.becareful.becarefulserver.domain.caregiver.domain.converter.CareTypeSetConverter;
import com.becareful.becarefulserver.domain.caregiver.domain.converter.DayOfWeekSetConverter;
import com.becareful.becarefulserver.domain.caregiver.domain.converter.WorkTimeSetConverter;
import com.becareful.becarefulserver.domain.common.domain.CareType;
import com.becareful.becarefulserver.domain.socialworker.domain.Elderly;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;

import java.time.DayOfWeek;
import java.util.EnumSet;

@Entity
@Getter
public class CompletedMatching {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "completed_matching_id")
    private Long id;


    @JoinColumn(name = "caregiver_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private Caregiver caregiver;

    @JoinColumn(name = "elderly_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private Elderly elderly;


    private String workDays;//계약서
    private String workStartTime;//계약서
    private String workEndTime;//계약서

    private String workSalaryType;//계약서
    private int workSalaryAmount;//계약서
    private String workAddress;//계약서 -> 매칭 -> 매칭 공고 -> 어르신 -> 주소
    @Convert(converter = CareTypeSetConverter.class)
    private EnumSet<CareType> workCareTypes;

    private String institutionName;
    private String institutionAddress;
    private String note;
}
