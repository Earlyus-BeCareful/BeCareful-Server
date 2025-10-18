package com.becareful.becarefulserver.domain.association.dto.response;

import com.becareful.becarefulserver.domain.association.domain.AssociationMember;
import com.becareful.becarefulserver.domain.association.domain.vo.AssociationRank;
import com.becareful.becarefulserver.domain.nursing_institution.domain.vo.InstitutionRank;

public record AssociationMemberResponse(
        Long memberId,
        String name,
        String phoneNumber,
        AssociationRank associationRank,
        InstitutionRank institutionRank,
        String institutionName,
        String institutionImageUrl) {
    public static AssociationMemberResponse from(AssociationMember member) {
        return new AssociationMemberResponse(
                member.getId(),
                member.getName(),
                member.getPhoneNumber(),
                member.getAssociationRank(),
                member.getInstitutionRank(),
                member.getNursingInstitution().getName(),
                member.getNursingInstitution().getProfileImageUrl());
    }
}
