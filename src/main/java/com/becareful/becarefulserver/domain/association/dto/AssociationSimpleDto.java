package com.becareful.becarefulserver.domain.association.dto;

import com.becareful.becarefulserver.domain.association.domain.Association;

public record AssociationSimpleDto(
        Long associationId,
        String associationName,
        Integer associationEstablishedYear,
        String associationProfileImageUrl,
        Integer associationMemberCount) {
    public static AssociationSimpleDto of(Association association, Integer memberCount) {
        return new AssociationSimpleDto(
                association.getId(),
                association.getName(),
                association.getEstablishedYear(),
                association.getProfileImageUrl(),
                memberCount);
    }
}
