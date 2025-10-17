package com.becareful.becarefulserver.domain.association.dto.request;

import com.becareful.becarefulserver.domain.association.domain.vo.AssociationRank;
import jakarta.validation.constraints.*;

public record UpdateAssociationChairmanRequest(
        @NotNull Long newChairmanId,
        @NotBlank String newChairmanName, // TODO : 필드 제거
        @NotNull AssociationRank nextRankOfCurrentChairman) {}
