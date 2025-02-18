package com.becareful.becarefulserver.domain.recruitment.dto.request;

import com.becareful.becarefulserver.domain.recruitment.domain.MediationType;

import java.util.List;

public record RecruitmentMediateRequest(
        List<MediationType> mediationTypes,
        String mediationDescription
) {}
