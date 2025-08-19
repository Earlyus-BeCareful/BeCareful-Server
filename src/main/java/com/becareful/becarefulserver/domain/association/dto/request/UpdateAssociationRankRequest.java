package com.becareful.becarefulserver.domain.association.dto.request;

import com.becareful.becarefulserver.domain.socialworker.domain.vo.AssociationRank;
import jakarta.validation.constraints.NotNull;

public record UpdateAssociationRankRequest(@NotNull Long memberId, @NotNull AssociationRank associationRank) {}
