package com.becareful.becarefulserver.domain.association.dto.response;

import com.becareful.becarefulserver.domain.association.domain.Association;

public record AssociationInfoResponse(Long associationId, String associationName, Integer associationMemberCount) {

    public static AssociationInfoResponse from(Association association, Integer associationMemberCount) {
        return new AssociationInfoResponse(association.getId(), association.getName(), associationMemberCount);
    }
}
