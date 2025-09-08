package com.becareful.becarefulserver.domain.association.dto.response;

import com.becareful.becarefulserver.domain.association.dto.AssociationSimpleDto;
import java.util.List;

public record AssociationSearchListResponse(Integer count, List<AssociationSimpleDto> associationSimpleDtoList) {

    public static AssociationSearchListResponse from(List<AssociationSimpleDto> associationSearchListResponse) {
        return new AssociationSearchListResponse(associationSearchListResponse.size(), associationSearchListResponse);
    }
}
