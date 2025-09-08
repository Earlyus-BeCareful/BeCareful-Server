package com.becareful.becarefulserver.domain.matching.dto;

import com.becareful.becarefulserver.domain.common.domain.CareType;
import java.util.List;

public record CareTypeDto(CareType careType, List<String> detailCareTypes) {}
