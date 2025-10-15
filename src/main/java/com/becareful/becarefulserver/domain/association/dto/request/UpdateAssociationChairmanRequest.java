package com.becareful.becarefulserver.domain.association.dto.request;

import com.becareful.becarefulserver.domain.association.domain.vo.AssociationRank;
import jakarta.validation.constraints.*;

public record UpdateAssociationChairmanRequest(
        @NotNull Long newChairmanId,
        @NotBlank String newChairmanName,
        @NotNull AssociationRank nextRankOfCurrentChairman) {}
