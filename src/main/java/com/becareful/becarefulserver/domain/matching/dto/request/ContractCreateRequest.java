package com.becareful.becarefulserver.domain.matching.dto.request;

import java.time.LocalDate;

public record ContractCreateRequest(Long matchingId, LocalDate workStartDate) {}
