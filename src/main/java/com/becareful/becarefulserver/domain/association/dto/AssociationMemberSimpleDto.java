package com.becareful.becarefulserver.domain.association.dto;

import com.becareful.becarefulserver.domain.association.domain.AssociationMember;
import com.becareful.becarefulserver.domain.nursing_institution.domain.vo.InstitutionRank;
import com.becareful.becarefulserver.domain.socialworker.domain.vo.AssociationRank;

public record AssociationMemberSimpleDto(
        Long memberId,
        String name,
        String phoneNumber,
        AssociationRank associationRank,
        String institutionName,
        InstitutionRank institutionRank,
        String institutionImageUrl) {
    public static AssociationMemberSimpleDto from(AssociationMember member) {
        return new AssociationMemberSimpleDto(
                member.getId(),
                member.getName(),
                member.getPhoneNumber(),
                member.getAssociationRank(),
                member.getInstitution().getName(),
                member.getInstitutionRank(),
                member.getInstitution().getProfileImageUrl());
    }
}
