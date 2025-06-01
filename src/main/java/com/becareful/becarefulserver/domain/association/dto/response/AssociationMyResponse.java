package com.becareful.becarefulserver.domain.association.dto.response;

import com.becareful.becarefulserver.domain.association.domain.Association;

public record AssociationMyResponse(Long associationId, String associationName, Integer associationMemberCount) {

    public static AssociationMyResponse from(Association association, Integer associationMemberCount) {
        return new AssociationMyResponse(association.getId(), association.getName(), associationMemberCount);
    }
}
