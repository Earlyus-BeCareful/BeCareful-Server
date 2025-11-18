package com.becareful.becarefulserver.domain.matching.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;

public record RecruitmentValidateDuplicatedRequest(
        @NotNull @Positive Long elderlyId,
        @NotNull @NotEmpty List<DayOfWeek> workDays,
        @Schema(example = "19:00") @NotNull LocalTime workStartTime, // TODO : Period 로 묶기
        @Schema(example = "21:00") @NotNull LocalTime workEndTime) {}
