package com.becareful.becarefulserver.domain.report.dto.request;

import com.becareful.becarefulserver.domain.report.domain.ReportType;
import jakarta.validation.constraints.NotNull;

public record ReportCreateRequest(@NotNull ReportType reportType, String description) {}
