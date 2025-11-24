package com.becareful.becarefulserver.domain.association.dto;

import com.becareful.becarefulserver.domain.association.domain.AssociationMember;
import com.becareful.becarefulserver.domain.community.domain.vo.CommunityAgreement;
import com.becareful.becarefulserver.domain.nursing_institution.domain.vo.InstitutionRank;
import com.becareful.becarefulserver.domain.association.domain.AssociationRank;

public record AssociationMemberDto(
        Long memberId,
        String name,
        String phoneNumber,
        AssociationRank associationRank,
        String institutionName,
        InstitutionRank institutionRank,
        String institutionImageUrl,
        CommunityAgreement communityAgreement,
        AssociationSimpleDto associationInfo) {
    public static AssociationMemberDto from(AssociationMember member) {
        return new AssociationMemberDto(
                member.getId(),
                member.getName(),
                member.getPhoneNumber(),
                member.getAssociationRank(),
                member.getInstitution().getName(),
                member.getInstitutionRank(),
                member.getInstitution().getProfileImageUrl(),
                member.getCommunityAgreement(),
                AssociationSimpleDto.from(member.getAssociation()));
    }
}
