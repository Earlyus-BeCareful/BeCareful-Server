package com.becareful.becarefulserver.domain.community.dto.response;

public record AssociationInfoResponse(
        String associationName,
        Long associationMemberCount
) {}
