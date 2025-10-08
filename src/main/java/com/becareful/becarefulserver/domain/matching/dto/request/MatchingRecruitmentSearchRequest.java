package com.becareful.becarefulserver.domain.matching.dto.request;

import jakarta.validation.constraints.NotBlank;

public record MatchingRecruitmentSearchRequest(@NotBlank String keyword) {}
