package com.becareful.becarefulserver.domain.association.dto;

import com.becareful.becarefulserver.domain.association.domain.Association;

public record AssociationSimpleDto(Long associationId, String associationName) {

    public static AssociationSimpleDto from(Association association) {
        return new AssociationSimpleDto(association.getId(), association.getName());
    }
}
