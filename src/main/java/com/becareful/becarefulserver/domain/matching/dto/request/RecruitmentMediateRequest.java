package com.becareful.becarefulserver.domain.matching.dto.request;

import com.becareful.becarefulserver.domain.matching.domain.MediationType;
import java.util.List;

public record RecruitmentMediateRequest(List<MediationType> mediationTypes, String mediationDescription) {}
