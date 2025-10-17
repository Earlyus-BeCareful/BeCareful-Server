package com.becareful.becarefulserver.domain.association.dto.response;

import java.util.List;

public record AssociationSearchListResponse(Integer count, List<AssociationResponse> associationResponseList) {

    public static AssociationSearchListResponse from(List<AssociationResponse> associationSearchListResponse) {
        return new AssociationSearchListResponse(associationSearchListResponse.size(), associationSearchListResponse);
    }
}
