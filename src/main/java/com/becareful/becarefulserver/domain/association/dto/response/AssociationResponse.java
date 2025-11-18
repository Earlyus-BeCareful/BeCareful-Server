package com.becareful.becarefulserver.domain.association.dto.response;

import com.becareful.becarefulserver.domain.association.domain.Association;

public record AssociationResponse(
        Long associationId,
        String associationName,
        Integer associationEstablishedYear,
        String associationProfileImageUrl,
        Integer associationMemberCount) {
    public static AssociationResponse of(Association association, Integer memberCount) {
        return new AssociationResponse(
                association.getId(),
                association.getName(),
                association.getEstablishedYear(),
                association.getProfileImageUrl(),
                memberCount);
    }
}
