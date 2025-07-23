package com.becareful.becarefulserver.domain.association.dto.request;

import com.becareful.becarefulserver.domain.socialworker.domain.vo.AssociationRank;

public record UpdateAssociationRankRequest(
        Long memberId,
        AssociationRank associationRank
) {
}
