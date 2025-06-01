package com.becareful.becarefulserver.domain.matching.dto.request;

import com.becareful.becarefulserver.domain.caregiver.domain.WorkSalaryType;
import com.becareful.becarefulserver.domain.common.domain.CareType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;

public record RecruitmentCreateRequest(
        @NotNull Long elderlyId,
        String title,
        List<DayOfWeek> workDays,
        @Schema(example = "19:00") LocalTime workStartTime,
        @Schema(example = "21:00") LocalTime workEndTime,
        List<CareType> careTypes,
        WorkSalaryType workSalaryType,
        Integer workSalaryAmount,
        String description) {}
