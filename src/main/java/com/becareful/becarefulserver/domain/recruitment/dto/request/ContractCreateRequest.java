package com.becareful.becarefulserver.domain.recruitment.dto.request;

import java.time.LocalDate;

public record ContractCreateRequest(
        Long matchingId,
        LocalDate workStartDate
) {
}
