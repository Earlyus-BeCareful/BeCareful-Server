package com.becareful.becarefulserver.domain.association.dto.request;

import com.becareful.becarefulserver.domain.socialworker.domain.vo.*;
import jakarta.validation.constraints.*;

public record UpdateAssociationChairmanRequest(
        @NotNull Long newChairmanId,
        @NotBlank String newChairmanName,
        @NotNull AssociationRank nextRankOfCurrentChairman) {}
