package com.becareful.becarefulserver.domain.report.dto.request;

import com.becareful.becarefulserver.domain.report.domain.ReportType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record ReportCreateRequest(
        @NotNull ReportTarget reportTarget,
        @NotNull @Positive Long reportTargetId,
        @NotNull ReportType reportType,
        String description) {
    public enum ReportTarget {
        POST,
        COMMENT,
        CHATROOM
    }
}
